package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_inbound_state",
        def = "{'sender': 1, 'recipient': 1, 'interchangeSequence' : 1, 'messageSequence': 1, 'transactionId': 1}",
        unique = true)
})
@Data
@Document
public class InboundState {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private WorkflowId workflowId;
    private String operationId;
    private Long interchangeSequence;
    private Long messageSequence;
    private String sender;
    private String recipient;
    private Long transactionId;
    private Instant translationTimestamp;
    private ReferenceTransactionType.Inbound transactionType;

    public static InboundState fromTransaction(Transaction transaction) {
        var interchangeHeader = transaction.getMessage().getInterchange().getInterchangeHeader();
        var translationDateTime = transaction.getMessage().getTranslationDateTime();
        var messageHeader = transaction.getMessage().getMessageHeader();
        var referenceTransactionNumber = transaction.getReferenceTransactionNumber();
        var referenceTransactionType = transaction.getMessage().getReferenceTransactionType();

        var recipient = interchangeHeader.getRecipient();
        var transactionNumber = referenceTransactionNumber.getTransactionNumber();

        return new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setOperationId(OperationId.buildOperationId(recipient, transactionNumber))
            .setSender(interchangeHeader.getSender())
            .setRecipient(recipient)
            .setInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setMessageSequence(messageHeader.getSequenceNumber())
            .setTransactionId(transactionNumber)
            .setTransactionType((ReferenceTransactionType.Inbound) referenceTransactionType.getTransactionType())
            .setTranslationTimestamp(translationDateTime.getTimestamp());
    }

    public static InboundState fromRecep(Message recep) {
        var interchangeHeader = recep.getInterchange().getInterchangeHeader();
        var messageHeader = recep.getMessageHeader();
        var dateTimePeriod = recep.getTranslationDateTime();

        return new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setMessageSequence(messageHeader.getSequenceNumber())
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setTranslationTimestamp(dateTimePeriod.getTimestamp());
    }
}
