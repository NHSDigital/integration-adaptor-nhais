package uk.nhs.digital.nhsconnect.nhais.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZonedDateTime;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InboundStateTest {

    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Instant TRANSLATION_TIMESTAMP = ZonedDateTime.now().toInstant();
    private static final long INTERCHANGE_SEQUENCE = 123L;
    private static final long MESSAGE_SEQUENCE = 234L;
    private static final long TRANSACTION_NUMBER = 345L;
    private static final ReferenceTransactionType.TransactionType TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.ACCEPTANCE;

    public static final Interchange INTERCHANGE = Mockito.mock(Interchange.class);
    public static final InboundState INBOUND_STATE = new InboundState()
        .setSender(SENDER)
        .setRecipient(RECIPIENT)
        .setReceiveInterchangeSequence(INTERCHANGE_SEQUENCE)
        .setReceiveMessageSequence(MESSAGE_SEQUENCE)
        .setTransactionNumber(TRANSACTION_NUMBER)
        .setTransactionType(TRANSACTION_TYPE)
        .setTranslationTimestamp(TRANSLATION_TIMESTAMP);

    @BeforeEach
    void setUp() {
        InterchangeHeader interchangeHeader = new InterchangeHeader(SENDER, RECIPIENT, TRANSLATION_TIMESTAMP).setSequenceNumber(INTERCHANGE_SEQUENCE);
        when(InboundStateTest.INTERCHANGE.getInterchangeHeader()).thenReturn(interchangeHeader);
        when(InboundStateTest.INTERCHANGE.getMessageHeader()).thenReturn(new MessageHeader().setSequenceNumber(MESSAGE_SEQUENCE));
        when(InboundStateTest.INTERCHANGE.getReferenceTransactionNumber()).thenReturn(new ReferenceTransactionNumber().setTransactionNumber(TRANSACTION_NUMBER));
        when(InboundStateTest.INTERCHANGE.getReferenceTransactionType()).thenReturn(new ReferenceTransactionType(TRANSACTION_TYPE));
    }


    @Test
    void whenFromInterchangeCalled_thenInboundStateObjectIsCreated() {
        var inboundStateFromInterchange = InboundState.fromInterchange(INTERCHANGE);

        assertThat(inboundStateFromInterchange).isEqualTo(INBOUND_STATE);
    }
}
