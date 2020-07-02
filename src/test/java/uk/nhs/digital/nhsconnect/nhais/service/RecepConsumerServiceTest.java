package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecepConsumerServiceTest {
    private static final String CONTENT = "some_content";
    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final long SIS = 10;
    private static final long SMS_1 = 100;
    private static final long SMS_2 = 200;
    private static final long SMS_3 = 300;
    private static final long REF_SIS_1 = 1000;
    private static final long REF_SIS_2 = 2000;
    private static final long REF_SMS_1 = 1100;
    private static final long REF_SMS_2 = 1200;
    private static final long REF_SMS_3 = 2100;
    private static final ReferenceMessageRecep.RecepCode REF_SMS_1_RECEP_CODE = ReferenceMessageRecep.RecepCode.ERROR;
    private static final ReferenceMessageRecep.RecepCode REF_SMS_2_RECEP_CODE = ReferenceMessageRecep.RecepCode.INCOMPLETE;
    private static final ReferenceMessageRecep.RecepCode REF_SMS_3_RECEP_CODE = ReferenceMessageRecep.RecepCode.SUCCESS;
    private static final Instant INTERCHANGE_TIMESTAMP = new TimestampService().getCurrentTimestamp();
    private static final Instant MESSAGE_1_TIMESTAMP = new TimestampService().getCurrentTimestamp().minusSeconds(10);
    private static final Instant MESSAGE_2_TIMESTAMP = new TimestampService().getCurrentTimestamp().plusSeconds(20);
    private final MeshMessage MESH_MESSAGE = new MeshMessage().setContent(CONTENT);
    @Mock
    private EdifactParser edifactParser;
    @Mock
    private OutboundStateRepository outboundStateRepository;
    @Mock
    private InboundStateRepository inboundStateRepository;
    @Mock
    private Interchange recep;
    @Mock
    private Message message1;
    @Mock
    private Message message2;
    @Mock
    private Message message3;
    @InjectMocks
    private RecepConsumerService recepConsumerService;

    @BeforeEach
    void setUp() {
        when(edifactParser.parse(CONTENT)).thenReturn(recep);

        when(recep.getInterchangeHeader())
            .thenReturn(new InterchangeHeader(SENDER, RECIPIENT, INTERCHANGE_TIMESTAMP).setSequenceNumber(SIS));
        when(recep.getMessages()).thenReturn(List.of(message1, message2, message3));

        when(message1.getInterchange()).thenReturn(recep);
        when(message1.getMessageHeader())
            .thenReturn(new MessageHeader().setSequenceNumber(SMS_1));
        when(message1.getTranslationDateTime())
            .thenReturn(new DateTimePeriod(MESSAGE_1_TIMESTAMP, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        when(message1.getReferenceInterchangeRecep())
            .thenReturn(new ReferenceInterchangeRecep(REF_SIS_1, ReferenceInterchangeRecep.RecepCode.RECEIVED, 2));
        when(message1.getReferenceMessageReceps())
            .thenReturn(List.of(
                new ReferenceMessageRecep(REF_SMS_1, REF_SMS_1_RECEP_CODE),
                new ReferenceMessageRecep(REF_SMS_2, REF_SMS_2_RECEP_CODE)));

        when(message2.getInterchange()).thenReturn(recep);
        when(message2.getMessageHeader())
            .thenReturn(new MessageHeader().setSequenceNumber(SMS_2));
        when(message2.getTranslationDateTime())
            .thenReturn(new DateTimePeriod(MESSAGE_2_TIMESTAMP, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        when(message2.getReferenceInterchangeRecep())
            .thenReturn(new ReferenceInterchangeRecep(REF_SIS_2, ReferenceInterchangeRecep.RecepCode.INVALID_DATA, 1));
        when(message2.getReferenceMessageReceps())
            .thenReturn(List.of(
                new ReferenceMessageRecep(REF_SMS_3, REF_SMS_3_RECEP_CODE)));

        when(message3.getInterchange()).thenReturn(recep);
        when(message3.getMessageHeader())
            .thenReturn(new MessageHeader().setSequenceNumber(SMS_3));

        when(inboundStateRepository.findBy(eq(WorkflowId.RECEP), eq(SENDER), eq(RECIPIENT), eq(SIS), any(), eq(null)))
            .thenReturn(Optional.empty());
        when(inboundStateRepository.findBy(eq(WorkflowId.RECEP), eq(SENDER), eq(RECIPIENT), eq(SIS), eq(SMS_3), eq(null)))
            .thenReturn(Optional.of(new InboundState()));
    }

    @Test
    void whenHandlingRecep_thenOutboundStateIsUpdatedAndInboundStateRecordedForNonDuplicateMessages() {
        recepConsumerService.handleRecep(MESH_MESSAGE);

        var inboundStateArgumentCaptor = ArgumentCaptor.forClass(InboundState.class);
        verify(inboundStateRepository, times(2)).save(inboundStateArgumentCaptor.capture());

        List<InboundState> inboundStateValues = inboundStateArgumentCaptor.getAllValues();
        assertThat(inboundStateValues).hasSize(2);
        assertInboundState(inboundStateValues.get(0), SMS_1, MESSAGE_1_TIMESTAMP);
        assertInboundState(inboundStateValues.get(1), SMS_2, MESSAGE_2_TIMESTAMP);

        var updateRecepParamsArgumentCaptor = ArgumentCaptor.forClass(OutboundStateRepositoryExtensions.UpdateRecepParams.class);
        verify(outboundStateRepository, times(3)).updateRecepDetails(updateRecepParamsArgumentCaptor.capture());

        var updateRecepParamsValues = updateRecepParamsArgumentCaptor.getAllValues();
        assertThat(updateRecepParamsValues).hasSize(3);
        assertOutboundStateUpdate(updateRecepParamsValues.get(0), REF_SIS_1, REF_SMS_1, REF_SMS_1_RECEP_CODE, MESSAGE_1_TIMESTAMP);
        assertOutboundStateUpdate(updateRecepParamsValues.get(1), REF_SIS_1, REF_SMS_2, REF_SMS_2_RECEP_CODE, MESSAGE_1_TIMESTAMP);
        assertOutboundStateUpdate(updateRecepParamsValues.get(2), REF_SIS_2, REF_SMS_3, REF_SMS_3_RECEP_CODE, MESSAGE_2_TIMESTAMP);
    }

    private void assertOutboundStateUpdate(
        OutboundStateRepositoryExtensions.UpdateRecepParams updateRecepParams,
        long refSis,
        long refSms,
        ReferenceMessageRecep.RecepCode recepCode,
        Instant recepDateTime) {

        assertThat(updateRecepParams.getSender()).isEqualTo(RECIPIENT);
        assertThat(updateRecepParams.getRecipient()).isEqualTo(SENDER);
        assertThat(updateRecepParams.getInterchangeSequence()).isEqualTo(refSis);
        assertThat(updateRecepParams.getMessageSequence()).isEqualTo(refSms);
        assertThat(updateRecepParams.getRecepCode()).isEqualTo(recepCode);
        assertThat(updateRecepParams.getRecepDateTime()).isEqualTo(recepDateTime);
    }

    private void assertInboundState(InboundState inboundState, long sms, Instant translationTimestamp) {
        assertThat(inboundState).isEqualTo(
            new InboundState()
                .setWorkflowId(WorkflowId.RECEP)
                .setInterchangeSequence(SIS)
                .setMessageSequence(sms)
                .setSender(SENDER)
                .setRecipient(RECIPIENT)
                .setTranslationTimestamp(translationTimestamp));
    }
}
