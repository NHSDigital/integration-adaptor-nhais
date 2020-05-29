package uk.nhs.digital.nhsconnect.nhais.repository;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InboundStateTest {

    private static final String OPERATION_ID = "bd0327c35d94d2972b4e0c99e355a8bb5ea2453eb27777d9e1985af38c9c2cf2";
    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Instant TRANSLATION_TIMESTAMP = ZonedDateTime.now().toInstant();
    private static final long INTERCHANGE_SEQUENCE = 123L;
    private static final long MESSAGE_SEQUENCE = 234L;
    private static final long TRANSACTION_NUMBER = 345L;
    private static final ReferenceTransactionType.TransactionType TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.ACCEPTANCE;

    public static final Interchange INTERCHANGE = new Interchange(ImmutableList.of(
        new InterchangeHeader(SENDER, RECIPIENT, TRANSLATION_TIMESTAMP)
            .setSequenceNumber(INTERCHANGE_SEQUENCE),
        new MessageHeader()
            .setSequenceNumber(MESSAGE_SEQUENCE),
        new ReferenceTransactionNumber()
            .setTransactionNumber(TRANSACTION_NUMBER),
        new ReferenceTransactionType(TRANSACTION_TYPE)));

    public static final InboundState INBOUND_STATE = new InboundState()
        .setOperationId(OPERATION_ID)
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

        assertEquals(INBOUND_STATE, inboundStateFromInterchange);
    }
}
