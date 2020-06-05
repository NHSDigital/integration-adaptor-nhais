package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_message",
        def = "{'sendInterchangeSequence' : 1, 'sendMessageSequence': 1, 'sender': 1, 'recipient': 1}",
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
    private Instant transactionTimestamp;
    private String transactionType;
    private Long sendInterchangeSequence;
    private Long sendMessageSequence;
    private String sender;
    private String recipient;
    private ReferenceMessageRecep.RecepCode recepCode;
    private Instant recepDateTime;

    public static OutboundState fromRecep(Recep recep) {
        var interchangeHeader = recep.getInterchangeHeader();
        var dateTimePeriod = recep.getDateTimePeriod();

        return new OutboundState()
            .setDataType(DataType.RECEP)
            .setSendInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setTransactionTimestamp(dateTimePeriod.getTimestamp());
    }
}
