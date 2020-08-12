package uk.nhs.digital.nhsconnect.nhais.inbound.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_inbound_state",
        def = "{'sndr': 1, 'recip': 1, 'intSeq' : 1, 'msgSeq': 1, 'tn': 1}",
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
    @Field(name = "intSeq")
    private Long interchangeSequence;
    @Field(name = "msgSeq")
    private Long messageSequence;
    @Field(name = "sndr")
    private String sender;
    @Field(name = "recip")
    private String recipient;
    @Field(name = "tn")
    private Long transactionNumber;
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
            .setTransactionNumber(transactionNumber)
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
