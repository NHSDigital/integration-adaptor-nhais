package uk.nhs.digital.nhsconnect.nhais.pcrm;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Document
public class OutboundState {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private String workflowId;
    private String operationId;
    @Field(name = "tn")
    private Long transactionNumber;
    private Instant translationTimestamp;
    private String transactionType;
    @Field(name = "intSeq")
    private Long interchangeSequence;
    @Field(name = "msgSeq")
    private Long messageSequence;
    @Field(name = "sndr")
    private String sender;
    @Field(name = "recip")
    private String recipient;
    private String recepCode;
    private Instant recepDateTime;
}
