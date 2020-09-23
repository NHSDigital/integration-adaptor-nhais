package uk.nhs.digital.nhsconnect.nhais.outbound.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.nhs.digital.nhsconnect.nhais.configuration.ttl.TimeToLive;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_outbound_state",
        def = "{'sndr': 1, 'recip': 1, 'intSeq' : 1, 'msgSeq': 1, 'tn': 1}",
        unique = true)
})
@Data
@Document
public class OutboundState implements TimeToLive {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private WorkflowId workflowId;
    private String operationId;
    @Field(name = "tn")
    private Long transactionNumber;
    private Instant translationTimestamp;
    private ReferenceTransactionType.Outbound transactionType;
    @Field(name = "intSeq")
    private Long interchangeSequence;
    @Field(name = "msgSeq")
    private Long messageSequence;
    @Field(name = "sndr")
    private String sender;
    @Field(name = "recip")
    private String recipient;
    private ReferenceMessageRecep.RecepCode recepCode;
    private Instant recepDateTime;

    public static OutboundState fromRecep(Message message) {
        var interchangeHeader = message.getInterchange().getInterchangeHeader();
        var messageHeader = message.getMessageHeader();
        var dateTimePeriod = message.getRecepTranslationDateTime();

        return new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setMessageSequence(messageHeader.getSequenceNumber())
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setTranslationTimestamp(dateTimePeriod.getTimestamp());
    }
}
