//package uk.nhs.digital.nhsconnect.nhais.service;
//
//import org.apache.commons.io.IOUtils;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
//import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.time.Instant;
//import java.time.ZonedDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@Disabled("NIAD-390")
//class RecepProducerServiceTest {
//    private static final String RECEP_EXAMPLE_PATH = "/edifact/recep_example.txt";
//    private static final String SENDER = "GP123";
//    private static final String RECIPIENT = "HA456";
//    private static final String REF_SENDER = RECIPIENT;
//    private static final String REF_RECIPIENT = SENDER;
//    private static final Long INTERCHANGE_SEQUENCE = 45L;
//    private static final Long MESSAGE_SEQUENCE_1 = 56L;
//    private static final Instant FIXED_TIME = ZonedDateTime
//        .of(2020, 4, 27, 17, 37, 0, 0, TimestampService.UKZone)
//        .toInstant();
//    private static final long RECEP_INTERCHANGE_SEQUENCE = 123123;
//    private static final long RECEP_MESSAGE_SEQUENCE = 234234;
//
//    @InjectMocks
//    RecepProducerService recepProducerService;
//
//    @Mock
//    SequenceService sequenceService;
//
//    @Test
//    public void whenProducingRecep_thenValidRecepIsCreated() throws IOException {
//        when(sequenceService.generateInterchangeId(REF_SENDER, REF_RECIPIENT)).thenReturn(RECEP_INTERCHANGE_SEQUENCE);
//        when(sequenceService.generateMessageId(REF_SENDER, REF_RECIPIENT)).thenReturn(RECEP_MESSAGE_SEQUENCE);
//
//        var recep = recepProducerService.produceRecep(createInterchange());
//
//        assertEquals(recep.toEdifact(), readFile(RECEP_EXAMPLE_PATH));
//
//        verify(sequenceService).generateInterchangeId(REF_SENDER, REF_RECIPIENT);
//        verify(sequenceService).generateMessageId(REF_SENDER, REF_RECIPIENT);
//
//        verifyNoMoreInteractions(sequenceService);
//    }
//
//    private Interchange createInterchange() {
//        var interchange = mock(Interchange.class);
//        var message = mock(Message.class);
//        var transaction = mock(Transaction.class);
//
//        when(interchange.getInterchangeHeader()).thenReturn(
//            new InterchangeHeader(SENDER, RECIPIENT, FIXED_TIME).setSequenceNumber(INTERCHANGE_SEQUENCE));
//        when(interchange.getInterchangeTrailer()).thenReturn(
//            new InterchangeTrailer(1));
//        when(interchange.getMessages()).thenReturn(List.of(message));
//        when(message.getMessageHeader()).thenReturn(
//            new MessageHeader().setSequenceNumber(MESSAGE_SEQUENCE_1));
//        when(message.getTranslationDateTime()).thenReturn(
//            new DateTimePeriod(FIXED_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
//        when(message.getTransactions()).thenReturn(List.of(transaction));
//        when(transaction.getMessage()).thenReturn(message);
//        return interchange;
//    }
//
//    private String readFile(String path) throws IOException {
//        try (InputStream is = this.getClass().getResourceAsStream(path)) {
//            return IOUtils.toString(is, StandardCharsets.UTF_8);
//        }
//    }
//}