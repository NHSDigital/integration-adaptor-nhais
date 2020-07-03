package uk.nhs.digital.nhsconnect.nhais.service;

import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationConsumerService {

    private final InboundGpSystemService inboundGpSystemService;
    private final InboundStateRepository inboundStateRepository;
    private final OutboundStateRepository outboundStateRepository;
    private final OutboundQueueService outboundQueueService;
    private final RecepProducerService recepProducerService;
    private final EdifactParser edifactParser;
    private final EdifactToFhirService edifactToFhirService;

    public void handleRegistration(InboundMeshMessage meshMessage) {
        LOGGER.debug("Received Registration message: {}", meshMessage);
        Interchange interchange = edifactParser.parse(meshMessage.getContent());

        var transactionsToProcess = filterOutDuplicates(interchange);

        var inboundStateRecords = prepareInboundStateRecords(transactionsToProcess);
        var supplierQueueDataToSend = prepareSupplierQueueDataToSend(transactionsToProcess);

        var recepEdifact = recepProducerService.produceRecep(interchange);
        var recep = edifactParser.parse(recepEdifact);
        var recepOutboundState = prepareRecepOutboundState(recep);
        var recepOutboundMessage = prepareRecepOutboundMessage(recepEdifact);

        Streams.zip(inboundStateRecords.stream(), supplierQueueDataToSend.stream(), Pair::of)
            .forEach(pair -> {
                inboundGpSystemService.publishToSupplierQueue(pair.getRight());
                inboundStateRepository.save(pair.getLeft());
            });

        outboundQueueService.publish(recepOutboundMessage);
        outboundStateRepository.save(recepOutboundState);
    }

    private List<Transaction> filterOutDuplicates(Interchange interchange) {
        return interchange.getMessages().stream()
            .map(Message::getTransactions)
            .flatMap(Collection::stream)
            .filter(transaction -> {
                boolean hasBeenProcessed = hasAlreadyBeenProcessed(transaction);
                if (!hasBeenProcessed) {
                    LOGGER.info("Skipping transaction {} as it has already been processed", transaction);
                }
                return !hasBeenProcessed;
            })
            .collect(Collectors.toList());
    }

    private OutboundState prepareRecepOutboundState(Interchange recep) {
        if (recep.getMessages().size() != 1) {
            throw new EdifactValidationException("Recep should have a 1 message");
        }
        return OutboundState.fromRecep(recep.getMessages().get(0));
    }

    private OutboundMeshMessage prepareRecepOutboundMessage(String recep) {
        var recepMeshMessage = buildRecepMeshMessage(recep);
        LOGGER.debug("Wrapped recep in mesh message: {}", recepMeshMessage);
        return recepMeshMessage;
    }

    private boolean hasAlreadyBeenProcessed(Transaction transaction) {
        var interchangeHeader = transaction.getMessage().getInterchange().getInterchangeHeader();
        var messageHeader = transaction.getMessage().getMessageHeader();

        var inboundState = inboundStateRepository.findBy(
            WorkflowId.REGISTRATION,
            interchangeHeader.getSender(),
            interchangeHeader.getRecipient(),
            interchangeHeader.getSequenceNumber(),
            messageHeader.getSequenceNumber(),
            transaction.getReferenceTransactionNumber().getTransactionNumber());

        return inboundState.isPresent();
    }

    private List<InboundGpSystemService.DataToSend> prepareSupplierQueueDataToSend(List<Transaction> transactions) {
        return transactions.stream()
            .map(transaction -> {
                LOGGER.debug("Handling transaction: {}", transaction);
                var outputParameters = edifactToFhirService.convertToFhir(transaction);
                LOGGER.debug("Converted registration message into FHIR: {}", outputParameters);
                var operationId = OperationId.buildOperationId(
                    transaction.getMessage().getInterchange().getInterchangeHeader().getRecipient(),
                    transaction.getReferenceTransactionNumber().getTransactionNumber());
                LOGGER.debug("Generated operation id: {}", operationId);
                return InboundGpSystemService.DataToSend.builder()
                    .parameters(outputParameters)
                    .operationId(operationId)
                    .transactionType(transaction.getMessage().getReferenceTransactionType().getTransactionType())
                    .build();
            }).collect(Collectors.toList());
    }

    private List<InboundState> prepareInboundStateRecords(List<Transaction> transactions) {
        return transactions.stream()
            .map(transaction -> {
                LOGGER.debug("Building transaction {} inbound state", transaction);
                return InboundState.fromTransaction(transaction);
            })
            .collect(Collectors.toList());
    }

    private OutboundMeshMessage buildRecepMeshMessage(String edifactRecep) {
        return new MeshMessage()
            // TODO: determine ODS code: probably via ENV? or should it be taken from incoming mesh message?
            .setHaTradingPartnerCode("ods123")
            .setWorkflowId(WorkflowId.RECEP)
            .setContent(edifactRecep);
    }
}
