package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class InboundStateTest {

    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Instant TRANSLATION_TIMESTAMP = ZonedDateTime.now().toInstant();
    private static final long INTERCHANGE_SEQUENCE = 123L;
    private static final long MESSAGE_SEQUENCE = 234L;
    private static final long TRANSACTION_NUMBER = 345L;
    private static final ReferenceTransactionType.TransactionType TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.ACCEPTANCE;

    public static final Interchange INTERCHANGE = Mockito.mock(Interchange.class);
    public static final Recep RECEP = Mockito.mock(Recep.class);
    public static final InboundState INBOUND_INTERCHANGE_STATE = new InboundState()
        .setDataType(DataType.INTERCHANGE)
        .setSender(SENDER)
        .setRecipient(RECIPIENT)
        .setReceiveInterchangeSequence(INTERCHANGE_SEQUENCE)
        .setReceiveMessageSequence(MESSAGE_SEQUENCE)
        .setTransactionNumber(TRANSACTION_NUMBER)
        .setTransactionType(TRANSACTION_TYPE)
        .setTranslationTimestamp(TRANSLATION_TIMESTAMP);
    public static final InboundState INBOUND_RECEP_STATE = new InboundState()
        .setDataType(DataType.RECEP)
        .setSender(SENDER)
        .setRecipient(RECIPIENT)
        .setReceiveInterchangeSequence(INTERCHANGE_SEQUENCE)
        .setTranslationTimestamp(TRANSLATION_TIMESTAMP);

    @BeforeEach
    void setUp() {
        InterchangeHeader interchangeHeader = new InterchangeHeader(SENDER, RECIPIENT, TRANSLATION_TIMESTAMP).setSequenceNumber(INTERCHANGE_SEQUENCE);
        when(InboundStateTest.INTERCHANGE.getInterchangeHeader()).thenReturn(interchangeHeader);
        when(InboundStateTest.INTERCHANGE.getMessageHeader()).thenReturn(new MessageHeader().setSequenceNumber(MESSAGE_SEQUENCE));
        when(InboundStateTest.INTERCHANGE.getReferenceTransactionNumber()).thenReturn(new ReferenceTransactionNumber().setTransactionNumber(TRANSACTION_NUMBER));
        when(InboundStateTest.INTERCHANGE.getReferenceTransactionType()).thenReturn(new ReferenceTransactionType(TRANSACTION_TYPE));

        when(InboundStateTest.RECEP.getInterchangeHeader()).thenReturn(interchangeHeader);
        when(InboundStateTest.RECEP.getReferenceInterchangeRecep()).thenReturn(
            new ReferenceInterchangeRecep(54343L, ReferenceInterchangeRecep.RecepCode.RECEIVED, 1));
        when(InboundStateTest.RECEP.getReferenceMessageReceps()).thenReturn(
            List.of(new ReferenceMessageRecep(456456L, ReferenceMessageRecep.RecepCode.SUCCESS)));
    }

    @Test
    void whenFromInterchangeCalled_thenInboundStateObjectIsCreated() {
        var inboundStateFromInterchange = InboundState.fromInterchange(INTERCHANGE);

        assertThat(inboundStateFromInterchange).isEqualTo(INBOUND_INTERCHANGE_STATE);
    }

    @Test
    void whenFromRecepCalled_thenInboundStateObjectIsCreated() {
        var inboundStateFromInterchange = InboundState.fromRecep(RECEP);

        assertThat(inboundStateFromInterchange).isEqualTo(INBOUND_RECEP_STATE);
    }
}
