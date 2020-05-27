package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter @Setter @EqualsAndHashCode @ToString
public class OutboundStateDAO {

    private String operationId;
    private Long transactionId;
    private Date transactionTimestamp;
    private String transactionType;
    private Long sendInterchangeSequence;
    private Long sendMessageSequence;
    private String sender;
    private String recipient;

}
