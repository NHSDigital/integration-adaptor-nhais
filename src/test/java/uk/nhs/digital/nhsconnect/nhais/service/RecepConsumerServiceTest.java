package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.RecepParser;
import uk.nhs.digital.nhsconnect.nhais.repository.DataType;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecepConsumerServiceTest {
    private static final String MESSAGE_CONTENT = "some_message_content";
    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final long INTERCHANGE_SEQUENCE = 123;
    private static final long REF_INTERCHANGE_SEQUENCE = 234;
    private static final long REF_MESSAGE_1_SEQUENCE = 345;
    private static final long REF_MESSAGE_2_SEQUENCE = 456;
    private static final ReferenceMessageRecep.RecepCode MESSAGE_1_RECEP_CODE = ReferenceMessageRecep.RecepCode.ERROR;
    private static final ReferenceMessageRecep.RecepCode MESSAGE_2_RECEP_CODE = ReferenceMessageRecep.RecepCode.INCOMPLETE;
    private static final Instant DATE_TIME_PERIOD = new TimestampService().getCurrentTimestamp();
    @Mock
    private RecepParser recepParser;
    @Mock
    private OutboundStateRepository outboundStateRepository;
    @Mock
    private InboundStateRepository inboundStateRepository;

    @InjectMocks
    private RecepConsumerService recepConsumerService;

    private final MeshMessage MESH_MESSAGE = new MeshMessage().setContent(MESSAGE_CONTENT);

    @Mock
    private Recep recep;

    @BeforeEach
    void setUp() {
        when(recep.getInterchangeHeader())
            .thenReturn(new InterchangeHeader(SENDER, RECIPIENT, DATE_TIME_PERIOD)
            .setSequenceNumber(INTERCHANGE_SEQUENCE));
        when(recep.getReferenceMessageReceps())
            .thenReturn(List.of(
                new ReferenceMessageRecep(REF_MESSAGE_1_SEQUENCE, MESSAGE_1_RECEP_CODE),
                new ReferenceMessageRecep(REF_MESSAGE_2_SEQUENCE, MESSAGE_2_RECEP_CODE)
            ));
        when(recep.getReferenceInterchangeRecep())
            .thenReturn(new ReferenceInterchangeRecep(REF_INTERCHANGE_SEQUENCE, ReferenceInterchangeRecep.RecepCode.RECEIVED, 2));
    }

    @Test
    void whenHandlingRecep_thenInboundStateIsRecordedOutboundStateIsUpdatedTwice() {
        when(recepParser.parse(MESSAGE_CONTENT)).thenReturn(recep);

        recepConsumerService.handleRecep(MESH_MESSAGE);

        ArgumentCaptor<InboundState> inboundStateArgumentCaptor = ArgumentCaptor.forClass(InboundState.class);
        verify(inboundStateRepository)
            .save(inboundStateArgumentCaptor.capture());

        assertThat(inboundStateArgumentCaptor.getValue()).isEqualTo(
            new InboundState()
            .setDataType(DataType.RECEP)
            .setReceiveInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setTranslationTimestamp(DATE_TIME_PERIOD));

        var queryParamsArgumentCaptor = ArgumentCaptor.forClass(OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams.class);
        var detailsArgumentCaptor = ArgumentCaptor.forClass(OutboundStateRepositoryExtensions.UpdateRecepDetails.class);

        verify(outboundStateRepository, times(2)).updateRecepDetails(queryParamsArgumentCaptor.capture(), detailsArgumentCaptor.capture());

        var queryParamsValues = queryParamsArgumentCaptor.getAllValues();
        var detailsValues = detailsArgumentCaptor.getAllValues();

        assertThat(queryParamsValues.size()).isEqualTo(2);
        assertThat(detailsValues.size()).isEqualTo(2);

        assertThat(queryParamsValues.get(0).getSender()).isEqualTo(SENDER);
        assertThat(queryParamsValues.get(0).getRecipient()).isEqualTo(RECIPIENT);
        assertThat(queryParamsValues.get(0).getInterchangeSequence()).isEqualTo(REF_INTERCHANGE_SEQUENCE);
        assertThat(queryParamsValues.get(0).getMessageSequence()).isEqualTo(REF_MESSAGE_1_SEQUENCE);
        assertThat(detailsValues.get(0).getRecepCode()).isEqualTo(MESSAGE_1_RECEP_CODE);
        assertThat(detailsValues.get(0).getRecepDateTime()).isEqualTo(DATE_TIME_PERIOD);

        assertThat(queryParamsValues.get(1).getSender()).isEqualTo(SENDER);
        assertThat(queryParamsValues.get(1).getRecipient()).isEqualTo(RECIPIENT);
        assertThat(queryParamsValues.get(1).getInterchangeSequence()).isEqualTo(REF_INTERCHANGE_SEQUENCE);
        assertThat(queryParamsValues.get(1).getMessageSequence()).isEqualTo(REF_MESSAGE_2_SEQUENCE);
        assertThat(detailsValues.get(1).getRecepCode()).isEqualTo(MESSAGE_2_RECEP_CODE);
        assertThat(detailsValues.get(1).getRecepDateTime()).isEqualTo(DATE_TIME_PERIOD);
    }
}
