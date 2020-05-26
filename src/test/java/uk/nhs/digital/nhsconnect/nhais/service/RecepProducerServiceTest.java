package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.*;

import java.time.ZonedDateTime;

class RecepProducerServiceTest {
    private static final String SENDER = "GP123";
    private static final String RECIPIENT = "HA456";
    private static final Long INTERCHANGE_SEQUENCE = 45L;
    private static final Long MESSAGE_SEQUENCE = 56L;
    private static final Long TRANSACTION_NUMBER = 5174L;

    @Autowired
    RecepProducerService recepProducerService;

    @Autowired
    private SequenceService sequenceService;

    @Test
    public void when_then() {
//        System.out.println(recepProducerService.mapEdifactToRecep());
        System.out.println("--------------- segments: ");
        createInterchange().getSegments().stream()
                .forEach(s -> System.out.println(s.getKey() + "+" + s.getValue()));
//        System.out.println(getEdifactMessage());
    }

    private Interchange createInterchange() {
        ZonedDateTime currentTime = ZonedDateTime.now();

        InterchangeHeader interchangeHeader = new InterchangeHeader(SENDER, RECIPIENT, currentTime);
        interchangeHeader.setSequenceNumber(INTERCHANGE_SEQUENCE);

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setSequenceNumber(MESSAGE_SEQUENCE);

        ReferenceTransactionNumber referenceTransactionNumber = new ReferenceTransactionNumber();
        referenceTransactionNumber.setTransactionNumber(TRANSACTION_NUMBER);

        MessageTrailer messageTrailer = new MessageTrailer(8);
        messageTrailer.setSequenceNumber(MESSAGE_SEQUENCE);

        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(1);
        interchangeTrailer.setSequenceNumber(INTERCHANGE_SEQUENCE);

        return Interchange.builder()
                .segment(interchangeHeader)
                .segment(messageHeader)
                .segment(new BeginningOfMessage())
                .segment(new NameAndAddress(RECIPIENT, NameAndAddress.QualifierAndCode.FHS))
                .segment(new DateTimePeriod(currentTime, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP))
                .segment(new ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE))
                .segment(new SegmentGroup(1))
                .segment(referenceTransactionNumber)
                .segment(messageTrailer)
                .segment(interchangeTrailer)
                .build();
    }
}