package uk.nhs.digital.nhsconnect.nhais.inbound.state;

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
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_inbound_state",
        def = "{'sndr': 1, 'recip': 1, 'intSeq' : 1, 'msgSeq': 1, 'tn': 1}",
        unique = true)
})
@Data
@Document
public class InboundState implements TimeToLive {
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
    private Instant processedTimestamp;
    private String conversationId;
}
