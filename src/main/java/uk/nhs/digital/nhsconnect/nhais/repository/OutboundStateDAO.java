package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.ZonedDateTime;
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
    private ReferenceMessageRecep.RecepCode recepCode;
    private ZonedDateTime recepDateTime;
}
