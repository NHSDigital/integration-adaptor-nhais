package uk.nhs.digital.nhsconnect.nhais.inbound;

import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.queue.InboundGpSystemService;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateFactory;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateFactory;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
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
    private final InboundStateFactory inboundStateFactory;
    private final OutboundStateRepository outboundStateRepository;
    private final OutboundStateFactory outboundStateFactory;
    private final OutboundQueueService outboundQueueService;
    private final RecepProducerService recepProducerService;
    private final EdifactParser edifactParser;
    private final InboundEdifactTransactionHandler inboundEdifactTransactionService;

    public void handleRegistration(InboundMeshMessage meshMessage) {
        Interchange interchange = edifactParser.parse(meshMessage.getContent());
        logInterchangeReceived(interchange);

        var transactionsToProcess = filterOutDuplicates(interchange);
        LOGGER.info("Interchange contains {} new transactions", transactionsToProcess.size());

        var inboundStateRecords = prepareInboundStateRecords(transactionsToProcess);
        var supplierQueueDataToSend = prepareSupplierQueueDataToSend(transactionsToProcess);

        var recepEdifact = recepProducerService.produceRecep(interchange);
        var recep = edifactParser.parse(recepEdifact);
        var recepOutboundState = prepareRecepOutboundState(recep);
        var recepOutboundMessage = prepareRecepOutboundMessage(recepEdifact, recep);

        Streams.forEachPair(inboundStateRecords.stream(), supplierQueueDataToSend.stream(),
            this::publishMessageAndRecordInboundState);

        outboundQueueService.publish(recepOutboundMessage);
        outboundStateRepository.save(recepOutboundState);
        logRecepSentFor(interchange);
    }

    private void publishMessageAndRecordInboundState(InboundState inboundState, InboundGpSystemService.DataToSend dataToSend) {
        if (isCloseQuarterNotification(dataToSend)) {
            LOGGER.info("Skipping publish to GP System Queue for Close Quarter Notification TN={}", inboundState.getTransactionNumber());
        } else {
            inboundGpSystemService.publishToSupplierQueue(dataToSend);
        }
        inboundStateRepository.save(inboundState);
        LOGGER.info("Completed processing TN={} OperationId={}", inboundState.getTransactionNumber(), inboundState.getOperationId());
    }

    private boolean isCloseQuarterNotification(InboundGpSystemService.DataToSend dataToSend) {
        return dataToSend.getTransactionType().equals(ReferenceTransactionType.Inbound.CLOSE_QUARTER_NOTIFICATION);
    }

    private List<Transaction> filterOutDuplicates(Interchange interchange) {
        return interchange.getMessages().stream()
            .map(Message::getTransactions)
            .flatMap(Collection::stream)
            .filter(transaction -> {
                boolean hasBeenProcessed = hasAlreadyBeenProcessed(transaction);
                if (hasBeenProcessed) {
                    LOGGER.warn("Skipping transaction {} as it has already been processed", transaction);
                }
                return !hasBeenProcessed;
            })
            .collect(Collectors.toList());
    }

    private OutboundState prepareRecepOutboundState(Interchange recep) {
        if (recep.getMessages().size() != 1) {
            throw new EdifactValidationException("Recep should have a 1 message");
        }
        return outboundStateFactory.fromRecep(recep.getMessages().get(0));
    }

    private OutboundMeshMessage prepareRecepOutboundMessage(String recepEdifact, Interchange recep) {
        var recepMeshMessage = buildRecepMeshMessage(recepEdifact, recep);
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
                var dataToSend = inboundEdifactTransactionService.translate(transaction);
                LOGGER.debug("Converted registration message into {}", dataToSend.getContent());
                var operationId = OperationId.buildOperationId(
                    transaction.getMessage().getInterchange().getInterchangeHeader().getRecipient(),
                    transaction.getReferenceTransactionNumber().getTransactionNumber());
                logTransactionReceived(transaction, operationId);
                return dataToSend
                    .setOperationId(operationId)
                    .setTransactionType(transaction.getMessage().getReferenceTransactionType().getTransactionType());
            }).collect(Collectors.toList());
    }

    private void logInterchangeReceived(Interchange interchange) {
        if(LOGGER.isInfoEnabled()) {
            var interchangeHeader = interchange.getInterchangeHeader();
            LOGGER.info("Translating EDIFACT interchange from Sender={} to Recipient={} with RIS={} containing {} messages",
                interchangeHeader.getSender(), interchangeHeader.getRecipient(), interchangeHeader.getSequenceNumber(),
                interchange.getMessages().size());
        }
    }

    private void logRecepSentFor(Interchange interchange) {
        if(LOGGER.isInfoEnabled()) {
            var interchangeHeader = interchange.getInterchangeHeader();
            LOGGER.info("Published for async send to MESH a RECEP for the interchange from Sender={} to Recipient={} with RIS={}",
                interchangeHeader.getSender(), interchangeHeader.getRecipient(), interchangeHeader.getSequenceNumber());
        }
    }

    private void logTransactionReceived(Transaction transaction, String operationId) {
        if(LOGGER.isInfoEnabled()) {
            var message = transaction.getMessage();
            var type = transaction.getMessage().getReferenceTransactionType().getTransactionType().getAbbreviation();
            LOGGER.info("Translating EDIFACT transaction TN={} OperationId={} of message Type={} RMS={}",
                transaction.getReferenceTransactionNumber().getTransactionNumber(), operationId,
                type, message.getMessageHeader().getSequenceNumber());
        }
    }

    private List<InboundState> prepareInboundStateRecords(List<Transaction> transactions) {
        return transactions.stream()
            .map(transaction -> {
                LOGGER.debug("Building transaction {} inbound state", transaction);
                return inboundStateFactory.fromTransaction(transaction);
            })
            .collect(Collectors.toList());
    }

    private OutboundMeshMessage buildRecepMeshMessage(String edifactRecep, Interchange recep) {
        return new MeshMessage()
            .setHaTradingPartnerCode(recep.getInterchangeHeader().getRecipient())
            .setWorkflowId(WorkflowId.RECEP)
            .setContent(edifactRecep);
    }
}
