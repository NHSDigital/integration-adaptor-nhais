package uk.nhs.digital.nhsconnect.nhais.outbound.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_outbound_state",
        def = "{'sender': 1, 'recipient': 1, 'interchangeSequence' : 1, 'messageSequence': 1, 'transactionId': 1}",
        unique = true)
})
@Data
@Document
public class OutboundState {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private WorkflowId workflowId;
    private String operationId;
    private Long transactionId;
    private Instant translationTimestamp;
    private ReferenceTransactionType.Outbound transactionType;
    private Long interchangeSequence;
    private Long messageSequence;
    private String sender;
    private String recipient;
    private ReferenceMessageRecep.RecepCode recepCode;
    private Instant recepDateTime;

    public static OutboundState fromRecep(Message message) {
        var interchangeHeader = message.getInterchange().getInterchangeHeader();
        var messageHeader = message.getMessageHeader();
        var dateTimePeriod = message.getTranslationDateTime();

        return new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setMessageSequence(messageHeader.getSequenceNumber())
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setTranslationTimestamp(dateTimePeriod.getTimestamp());
    }
}
