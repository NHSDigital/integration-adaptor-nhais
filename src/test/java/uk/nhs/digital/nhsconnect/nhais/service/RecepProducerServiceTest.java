package uk.nhs.digital.nhsconnect.nhais.service;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecepProducerServiceTest {
    private static final String INBOUND_EXAMPLE_PATH = "/edifact/inbound_example.txt";
    private static final String SENDER = "GP123";
    private static final String RECIPIENT = "HA456";
    private static final Long INTERCHANGE_SEQUENCE = 45L;
    private static final Long MESSAGE_SEQUENCE = 56L;
    private static final Long TRANSACTION_NUMBER = 5174L;

    private static final ZonedDateTime FIXED_TIME = ZonedDateTime.of(
            2020,
            4,
            27,
            17,
            37,
            0,
            0,
            ZoneId.of("GMT"));

    @Autowired
    RecepProducerService recepProducerService;

    @Test
    public void When_CreatingInterchange_Then_ItsTheSameAsExample() throws IOException {
        assertEquals(createInterchange().toString(), readFile(INBOUND_EXAMPLE_PATH));
    }

    @Test
    public void when_then() {
//        System.out.println(recepProducerService.mapEdifactToRecep());
        System.out.println("--------------- segments: ");
        createInterchange().getSegments().stream()
                .forEach(s -> System.out.println(s.getKey() + "+" + s.getValue()));
//        System.out.println(getEdifactMessage());
    }

    private Interchange createInterchange() {
        InterchangeHeader interchangeHeader = new InterchangeHeader(SENDER, RECIPIENT, FIXED_TIME);
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
                .segment(new DateTimePeriod(FIXED_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP))
                .segment(new ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE))
                .segment(new SegmentGroup(1))
                .segment(referenceTransactionNumber)
                .segment(messageTrailer)
                .segment(interchangeTrailer)
                .build();
    }

    private String readFile(String path) throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream(path)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }
}