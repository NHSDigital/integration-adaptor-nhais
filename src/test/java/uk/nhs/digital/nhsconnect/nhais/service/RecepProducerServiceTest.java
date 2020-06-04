package uk.nhs.digital.nhsconnect.nhais.service;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecepProducerServiceTest {
    private static final String INBOUND_EXAMPLE_PATH = "/edifact/inbound_example.txt";
    private static final String RECEP_EXAMPLE_PATH = "/edifact/recep_example.txt";
    private static final String SENDER = "GP123";
    private static final String RECIPIENT = "HA456";
    private static final Long INTERCHANGE_SEQUENCE = 45L;
    private static final Long MESSAGE_SEQUENCE_1 = 56L;
    private static final Long TRANSACTION_NUMBER_1 = 5174L;
    private static final Long MESSAGE_SEQUENCE_2 = 57L;
    private static final Long TRANSACTION_NUMBER_2 = 5175L;

    private static final Instant FIXED_TIME = ZonedDateTime.of(
            2020,
            4,
            27,
            17,
            37,
            0,
            0,
            ZoneId.of("GMT")).toInstant();

    @InjectMocks
    RecepProducerService recepProducerService;

    @Mock
    private OutboundStateRepository outboundStateRepository;

    @Test
    public void When_CreatingInterchange_Then_ItsTheSameAsExample() throws IOException {
        assertEquals(createInterchange().toString(), readFile(INBOUND_EXAMPLE_PATH));
    }

    @Test
    public void When_MapToRecep_Then_ExpectCorrectMessage() throws IOException {
        assertEquals(recepProducerService.produceRecep(createInterchange()).toString(), readFile(RECEP_EXAMPLE_PATH));
    }

    private Interchange createInterchange() {
        InterchangeHeader interchangeHeader = new InterchangeHeader(SENDER, RECIPIENT, FIXED_TIME);
        interchangeHeader.setSequenceNumber(INTERCHANGE_SEQUENCE);

        MessageHeader messageHeader1 = new MessageHeader();
        messageHeader1.setSequenceNumber(MESSAGE_SEQUENCE_1);
        ReferenceTransactionNumber referenceTransactionNumber1 = new ReferenceTransactionNumber();
        referenceTransactionNumber1.setTransactionNumber(TRANSACTION_NUMBER_1);
        MessageTrailer messageTrailer1 = new MessageTrailer(8);
        messageTrailer1.setSequenceNumber(MESSAGE_SEQUENCE_1);

        MessageHeader messageHeader2 = new MessageHeader();
        messageHeader2.setSequenceNumber(MESSAGE_SEQUENCE_2);
        ReferenceTransactionNumber referenceTransactionNumber2 = new ReferenceTransactionNumber();
        referenceTransactionNumber2.setTransactionNumber(TRANSACTION_NUMBER_2);
        MessageTrailer messageTrailer2 = new MessageTrailer(8);
        messageTrailer2.setSequenceNumber(MESSAGE_SEQUENCE_2);

        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(2);
        interchangeTrailer.setSequenceNumber(INTERCHANGE_SEQUENCE);

        var interchange = mock(Interchange.class);
        when(interchange.getInterchangeHeader()).thenReturn(interchangeHeader);
        when(interchange.getMessageHeader()).thenReturn(messageHeader1);
//                .segment(new BeginningOfMessage())
        when(interchange.getNameAndAddress()).thenReturn(
            new NameAndAddress(RECIPIENT, NameAndAddress.QualifierAndCode.FHS));
        when(interchange.getTranslationDateTime()).thenReturn(
            new DateTimePeriod(FIXED_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        when(interchange.getReferenceTransactionType()).thenReturn(
            new ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE));
//                .segment(new SegmentGroup(1))
        when(interchange.getReferenceTransactionNumber()).thenReturn(referenceTransactionNumber1);
//                .segment(messageTrailer1)
//                .segment(messageHeader2)
//                .segment(new BeginningOfMessage())
//                .segment(new NameAndAddress(RECIPIENT, NameAndAddress.QualifierAndCode.FHS))
//                .segment(new DateTimePeriod(FIXED_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP))
//                .segment(new ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE))
//                .segment(new SegmentGroup(1))
//                .segment(referenceTransactionNumber2)
//                .segment(messageTrailer2)
//                .segment(interchangeTrailer)
//                .build();
        return interchange;
    }

    private String readFile(String path) throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream(path)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }
}