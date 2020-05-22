package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter @Setter
public class OutboundStateDAO {

    private String operationId;
    private String transactionId;
    private ZonedDateTime transactionTimestamp;
    private String transactionType;
    private String sendInterchangeSequence;
    private String sendMessageSequence;
    private String sender;
    private String recipient;

}
