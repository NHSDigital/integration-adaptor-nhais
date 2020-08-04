package uk.nhs.digital.nhsconnect.nhais.outbound.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
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
public class OutboundState {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private WorkflowId workflowId;
    private String operationId;
    private Long tn;
    private Instant translationTimestamp;
    private ReferenceTransactionType.Outbound transactionType;
    private Long intSeq;
    private Long msgSeq;
    private String sndr;
    private String recip;
    private ReferenceMessageRecep.RecepCode recepCode;
    private Instant recepDateTime;

    public static OutboundState fromRecep(Message message) {
        var interchangeHeader = message.getInterchange().getInterchangeHeader();
        var messageHeader = message.getMessageHeader();
        var dateTimePeriod = message.getTranslationDateTime();

        return new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setIntSeq(interchangeHeader.getSequenceNumber())
            .setMsgSeq(messageHeader.getSequenceNumber())
            .setSndr(interchangeHeader.getSender())
            .setRecip(interchangeHeader.getRecipient())
            .setTranslationTimestamp(dateTimePeriod.getTimestamp());
    }
}
