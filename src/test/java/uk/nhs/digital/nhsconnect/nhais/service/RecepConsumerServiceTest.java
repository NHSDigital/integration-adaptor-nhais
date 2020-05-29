package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecepConsumerServiceTest {
    private static final String MESSAGE_CONTENT = "some_message_content";
    private static final String SENDER = "some_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final long INTERCHANGE_SEQUENCE = 345;
    private static final long MESSAGE_1_SEQUENCE = 123L;
    private static final long MESSAGE_2_SEQUENCE = 234;
    private static final ReferenceMessageRecep.RecepCode MESSAGE_1_RECEP_CODE = ReferenceMessageRecep.RecepCode.CA;
    private static final ReferenceMessageRecep.RecepCode MESSAGE_2_RECEP_CODE = ReferenceMessageRecep.RecepCode.CI;
    private static final Instant DATE_TIME_PERIOD = new TimestampService().getCurrentTimestamp();
    @Mock
    private EdifactParser edifactParser;
    @Mock
    private OutboundStateRepository outboundStateRepository;

    @InjectMocks
    private RecepConsumerService recepConsumerService;

    private final MeshMessage MESH_MESSAGE = new MeshMessage().setContent(MESSAGE_CONTENT);

    @Spy
    private final Interchange INTERCHANGE = new Interchange(List.of(
        new InterchangeHeader(SENDER, RECIPIENT, DATE_TIME_PERIOD).setSequenceNumber(INTERCHANGE_SEQUENCE),
        new DateTimePeriod(DATE_TIME_PERIOD, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP),
        new ReferenceMessageRecep(MESSAGE_1_SEQUENCE, MESSAGE_1_RECEP_CODE),
        new ReferenceMessageRecep(MESSAGE_2_SEQUENCE, MESSAGE_2_RECEP_CODE)
    ));

    @Test
    void whenHandlingRecep_thenOutboundStateIsUpdatedTwice() {
        when(edifactParser.parse(MESSAGE_CONTENT)).thenReturn(INTERCHANGE);

        recepConsumerService.handleRecep(MESH_MESSAGE);

        var queryParamsArgumentCaptor = ArgumentCaptor.forClass(OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams.class);
        var detailsArgumentCaptor = ArgumentCaptor.forClass(OutboundStateRepositoryExtensions.UpdateRecepDetails.class);

        verify(outboundStateRepository, times(2)).updateRecepDetails(queryParamsArgumentCaptor.capture(), detailsArgumentCaptor.capture());

        var queryParamsValues = queryParamsArgumentCaptor.getAllValues();
        var detailsValues = detailsArgumentCaptor.getAllValues();

        assertEquals(2, queryParamsValues.size());
        assertEquals(2, detailsValues.size());

        assertEquals(SENDER, queryParamsValues.get(0).getSender());
        assertEquals(RECIPIENT, queryParamsValues.get(0).getRecipient());
        assertEquals(INTERCHANGE_SEQUENCE, queryParamsValues.get(0).getInterchangeSequence());
        assertEquals(MESSAGE_1_SEQUENCE, queryParamsValues.get(0).getMessageSequence());
        assertEquals(MESSAGE_1_RECEP_CODE, detailsValues.get(0).getRecepCode());
        assertEquals(DATE_TIME_PERIOD, detailsValues.get(0).getRecepDateTime());

        assertEquals(SENDER, queryParamsValues.get(1).getSender());
        assertEquals(RECIPIENT, queryParamsValues.get(1).getRecipient());
        assertEquals(INTERCHANGE_SEQUENCE, queryParamsValues.get(1).getInterchangeSequence());
        assertEquals(MESSAGE_2_SEQUENCE, queryParamsValues.get(1).getMessageSequence());
        assertEquals(MESSAGE_2_RECEP_CODE, detailsValues.get(1).getRecepCode());
        assertEquals(DATE_TIME_PERIOD, detailsValues.get(1).getRecepDateTime());
    }
}
