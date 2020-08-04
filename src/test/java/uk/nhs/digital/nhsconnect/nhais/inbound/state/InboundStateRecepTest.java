package uk.nhs.digital.nhsconnect.nhais.inbound.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class InboundStateRecepTest {
    public static final Message MESSAGE = Mockito.mock(Message.class);
    public static final Interchange RECEP = Mockito.mock(Interchange.class);
    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Instant INTERCHANGE_TIMESTAMP = ZonedDateTime.now().toInstant();
    private static final Instant TRANSLATION_TIMESTAMP = INTERCHANGE_TIMESTAMP.plusSeconds(10);
    private static final long INTERCHANGE_SEQUENCE = 123L;
    private static final long MESSAGE_SEQUENCE = 234L;
    public static final InboundState EXPECTED_RECEP_INBOUND_STATE = new InboundState()
        .setWorkflowId(WorkflowId.RECEP)
        .setSndr(SENDER)
        .setRecip(RECIPIENT)
        .setIntSeq(INTERCHANGE_SEQUENCE)
        .setMsgSeq(MESSAGE_SEQUENCE)
        .setTranslationTimestamp(TRANSLATION_TIMESTAMP);

    @BeforeEach
    void setUp() {
        when(RECEP.getInterchangeHeader()).thenReturn(
            new InterchangeHeader(SENDER, RECIPIENT, INTERCHANGE_TIMESTAMP).setSequenceNumber(INTERCHANGE_SEQUENCE));
        when(RECEP.getMessages()).thenReturn(List.of(MESSAGE));
        when(MESSAGE.getInterchange()).thenReturn(RECEP);
        when(MESSAGE.getMessageHeader()).thenReturn(
            new MessageHeader().setSequenceNumber(MESSAGE_SEQUENCE));
        when(MESSAGE.getTranslationDateTime()).thenReturn(
            new DateTimePeriod(TRANSLATION_TIMESTAMP, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        when(MESSAGE.getReferenceInterchangeRecep()).thenReturn(
            new ReferenceInterchangeRecep(54343L, ReferenceInterchangeRecep.RecepCode.RECEIVED, 1));
        when(MESSAGE.getReferenceMessageReceps()).thenReturn(
            List.of(new ReferenceMessageRecep(456456L, ReferenceMessageRecep.RecepCode.SUCCESS)));
    }

    @Test
    void whenFromRecepCalled_thenInboundStateObjectIsCreated() {
        var inboundStateFromInterchange = InboundState.fromRecep(MESSAGE);

        assertThat(inboundStateFromInterchange).isEqualTo(EXPECTED_RECEP_INBOUND_STATE);
    }
}
