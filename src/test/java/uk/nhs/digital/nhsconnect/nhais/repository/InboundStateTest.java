package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InboundStateTest {

    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final ZonedDateTime TRANSLATION_TIMESTAMP = ZonedDateTime.now();
    private static final long INTERCHANGE_SEQUENCE = 123L;
    private static final long MESSAGE_SEQUENCE = 234L;
    private static final long TRANSACTION_NUMBER = 345L;
    private static final ReferenceTransactionType.TransactionType TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.ACCEPTANCE;

    public static final Interchange INTERCHANGE = new Interchange(List.of(
        new InterchangeHeader(SENDER, RECIPIENT, TRANSLATION_TIMESTAMP)
            .setSequenceNumber(INTERCHANGE_SEQUENCE),
        new MessageHeader()
            .setSequenceNumber(MESSAGE_SEQUENCE),
        new ReferenceTransactionNumber()
            .setTransactionNumber(TRANSACTION_NUMBER),
        new ReferenceTransactionType(TRANSACTION_TYPE)));

    public static final InboundState INBOUND_STATE = new InboundState()
        .setSender(SENDER)
        .setRecipient(RECIPIENT)
        .setReceiveInterchangeSequence(INTERCHANGE_SEQUENCE)
        .setReceiveMessageSequence(MESSAGE_SEQUENCE)
        .setTransactionNumber(TRANSACTION_NUMBER)
        .setTransactionType(TRANSACTION_TYPE)
        .setTranslationTimestamp(TRANSLATION_TIMESTAMP);

    @Test
    void whenFromInterchangeCalled_thenInboundStateObjectIsCreated() {
        var inboundStateFromInterchange = InboundState.fromInterchange(INTERCHANGE);

        assertEquals(inboundStateFromInterchange, INBOUND_STATE);
    }
}
