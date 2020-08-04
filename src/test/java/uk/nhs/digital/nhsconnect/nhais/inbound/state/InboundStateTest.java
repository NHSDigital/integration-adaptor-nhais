package uk.nhs.digital.nhsconnect.nhais.inbound.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class InboundStateTest {

    public static final Interchange INTERCHANGE = Mockito.mock(Interchange.class);
    public static final Message MESSAGE = Mockito.mock(Message.class);
    public static final Transaction TRANSACTION = Mockito.mock(Transaction.class);
    private static final String OPERATION_ID = "4b93239acaf902960fad67a339cfda2c1c0f771d51122627066cfcc667bc6b16";
    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Instant INTERCHANGE_TIMESTAMP = ZonedDateTime.now().toInstant();
    private static final Instant TRANSLATION_TIMESTAMP = INTERCHANGE_TIMESTAMP.plusSeconds(10);
    private static final long INTERCHANGE_SEQUENCE = 123L;
    private static final long MESSAGE_SEQUENCE = 234L;
    private static final long TRANSACTION_NUMBER = 345L;
    private static final ReferenceTransactionType.Inbound TRANSACTION_TYPE = ReferenceTransactionType.Inbound.APPROVAL;
    public static final InboundState EXPECTED_INTERCHANGE_INBOUND_STATE = new InboundState()
        .setWorkflowId(WorkflowId.REGISTRATION)
        .setOperationId(OPERATION_ID)
        .setSndr(SENDER)
        .setRecip(RECIPIENT)
        .setIntSeq(INTERCHANGE_SEQUENCE)
        .setMsgSeq(MESSAGE_SEQUENCE)
        .setTn(TRANSACTION_NUMBER)
        .setTransactionType(TRANSACTION_TYPE)
        .setTranslationTimestamp(TRANSLATION_TIMESTAMP);

    @BeforeEach
    void setUp() {
        when(INTERCHANGE.getInterchangeHeader()).thenReturn(new InterchangeHeader(SENDER, RECIPIENT, INTERCHANGE_TIMESTAMP).setSequenceNumber(INTERCHANGE_SEQUENCE));
        when(INTERCHANGE.getMessages()).thenReturn(List.of(MESSAGE));

        when(MESSAGE.getMessageHeader()).thenReturn(new MessageHeader().setSequenceNumber(MESSAGE_SEQUENCE));
        when(MESSAGE.getReferenceTransactionType()).thenReturn(new ReferenceTransactionType(TRANSACTION_TYPE));
        when(MESSAGE.getTranslationDateTime()).thenReturn(new DateTimePeriod(TRANSLATION_TIMESTAMP, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        when(MESSAGE.getInterchange()).thenReturn(INTERCHANGE);
        when(MESSAGE.getTransactions()).thenReturn(List.of(TRANSACTION));

        when(TRANSACTION.getReferenceTransactionNumber()).thenReturn(new ReferenceTransactionNumber().setTransactionNumber(TRANSACTION_NUMBER));
        when(TRANSACTION.getMessage()).thenReturn(MESSAGE);
    }

    @Test
    void whenFromInterchangeCalled_thenInboundStateObjectIsCreated() {
        var inboundStateFromInterchange = InboundState.fromTransaction(TRANSACTION);

        assertThat(inboundStateFromInterchange).isEqualTo(EXPECTED_INTERCHANGE_INBOUND_STATE);
    }
}
