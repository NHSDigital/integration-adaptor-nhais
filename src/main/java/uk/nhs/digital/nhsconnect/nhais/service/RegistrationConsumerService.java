package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.InterchangeV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.MessageV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParserV2;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationConsumerService {

    private final InboundGpSystemService inboundGpSystemService;
    private final InboundStateRepository inboundStateRepository;
    private final OutboundStateRepository outboundStateRepository;
    private final OutboundMeshService outboundMeshService;
    private final RecepProducerService recepProducerService;
    private final EdifactParserV2 edifactParser;
    private final EdifactToFhirService edifactToFhirService;

    public void handleRegistration(MeshMessage meshMessage) {
        LOGGER.debug("Received Registration message: {}", meshMessage);
        InterchangeV2 interchange = edifactParser.parse(meshMessage.getContent());

        var transactionsToProcess = interchange.getMessages().stream()
            .map(MessageV2::getTransactions)
            .flatMap(Collection::stream)
            .filter(this::isNewTransaction)
            .collect(Collectors.toList());

        var inboundStateRecords = prepareInboundStateRecords(transactionsToProcess);
        var supplierQueueDataToSend = prepareSupplierQueueDataToSend(transactionsToProcess);
        var recepOutboundState = prepareRecepOutboundState(interchange);
        var recepOutboundMessage = prepareRecepOutboundMessage(interchange);

        supplierQueueDataToSend.forEach(inboundGpSystemService::publishToSupplierQueue);
        inboundStateRecords.forEach(inboundStateRepository::save);

//        outboundMeshService.publishToOutboundQueue(recepOutboundMessage);
//        outboundStateRepository.save(recepOutboundState);
    }

    private MeshMessage prepareRecepOutboundMessage(InterchangeV2 interchange) {
//        var recepMeshMessage = buildRecepMeshMessage(recep);
//        LOGGER.debug("Wrapped recep in mesh message: {}", recepMeshMessage);
//        outboundMeshService.publishToOutboundQueue(recepMeshMessage);
//        LOGGER.debug("Published recep to outbound queue");
        return null;
    }

    private OutboundState prepareRecepOutboundState(InterchangeV2 interchange) {
//        var recep = recepProducerService.produceRecep(interchange);
//        var recepOutboundState = OutboundState.fromRecep(recep);
//        outboundStateRepository.save(recepOutboundState);
//        LOGGER.debug("Saved recep in outbound state: {}", recepOutboundState);
        return null;
    }

    private boolean isNewTransaction(TransactionV2 transaction) {
        var interchangeHeader = transaction.getMessage().getInterchange().getInterchangeHeader();
        var messageHeader = transaction.getMessage().getMessageHeader();

        var inboundState = inboundStateRepository.findBy(
            WorkflowId.REGISTRATION,
            interchangeHeader.getSender(),
            interchangeHeader.getRecipient(),
            interchangeHeader.getSequenceNumber(),
            messageHeader.getSequenceNumber(),
            transaction.getReferenceTransactionNumber().getTransactionNumber());

        return inboundState != null;
    }

    private Stream<InboundGpSystemService.DataToSend> prepareSupplierQueueDataToSend(List<TransactionV2> transactions) {
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
            });
    }

    private Stream<InboundState> prepareInboundStateRecords(List<TransactionV2> transactions) {
        return transactions.stream().map(transaction -> {
            LOGGER.debug("Building transaction {} inbound state", transaction);
            return InboundState.fromTransaction(transaction);
        });
    }

    private MeshMessage buildRecepMeshMessage(Recep recep) {
        return new MeshMessage()
            // TODO: determine ODS code: probably via ENV? or should it be taken from incoming mesh message?
            .setOdsCode("ods123")
            .setWorkflowId(WorkflowId.RECEP)
            .setContent(recep.toEdifact());
    }
}
