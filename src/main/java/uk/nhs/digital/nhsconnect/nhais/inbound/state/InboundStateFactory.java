package uk.nhs.digital.nhsconnect.nhais.inbound.state;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.InboundOperationIdService;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InboundStateFactory {

    private final TimestampService timestampService;
    private final ConversationIdService conversationIdService;
    private final InboundOperationIdService inboundOperationIdService;

    public InboundState fromTransaction(Transaction transaction) {
        var interchangeHeader = transaction.getMessage().getInterchange().getInterchangeHeader();
        var translationTimestamp = interchangeHeader.getTranslationTime();
        var messageHeader = transaction.getMessage().getMessageHeader();
        var referenceTransactionNumber = transaction.getReferenceTransactionNumber();
        var referenceTransactionType = transaction.getMessage().getReferenceTransactionType();
        var recipient = interchangeHeader.getRecipient();
        var transactionNumber = referenceTransactionNumber.getTransactionNumber();
        var processedTimestamp = timestampService.getCurrentTimestamp();

        return new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setOperationId(inboundOperationIdService.createOperationIdForTransaction(transaction))
            .setSender(interchangeHeader.getSender())
            .setRecipient(recipient)
            .setInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setMessageSequence(messageHeader.getSequenceNumber())
            .setTransactionNumber(transactionNumber)
            .setTransactionType((ReferenceTransactionType.Inbound) referenceTransactionType.getTransactionType())
            .setTranslationTimestamp(translationTimestamp)
            .setProcessedTimestamp(processedTimestamp)
            .setConversationId(conversationIdService.getCurrentConversationId());
    }

    public InboundState fromRecep(Message recep) {
        var interchangeHeader = recep.getInterchange().getInterchangeHeader();
        var messageHeader = recep.getMessageHeader();
        var translationTimestamp = interchangeHeader.getTranslationTime();
        var processedTimestamp = timestampService.getCurrentTimestamp();

        return new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setMessageSequence(messageHeader.getSequenceNumber())
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setTranslationTimestamp(translationTimestamp)
            .setProcessedTimestamp(processedTimestamp)
            .setConversationId(conversationIdService.getCurrentConversationId());
    }

}
