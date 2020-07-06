package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.InboundQueueService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeshServiceTest {

    @Mock
    private MeshClient meshClient;

    @Mock
    private InboundQueueService inboundQueueService;

    @Mock
    private MeshMailBoxScheduler meshMailBoxScheduler;

    private long scanDelayInSeconds = 5L;
    private static final String MESSAGE_ID = "messageId";
    private static final String ERROR_MESSAGE_ID = "messageId_2";
    private MeshService meshService;
    private MeshMessage meshMessage;

    @BeforeEach
    public void setUp() {
        meshMessage = new MeshMessage();
        meshMessage.setMeshMessageId(MESSAGE_ID);

        meshService = new MeshService(meshClient,
                inboundQueueService,
                meshMailBoxScheduler,
                scanDelayInSeconds);
    }

    @Test
    public void When_IntervalPassedAndMessagesFound_Then_DownloadAndPublishMessage() {
        MeshService meshService = new MeshService(meshClient,
                inboundQueueService,
                meshMailBoxScheduler,
                scanDelayInSeconds);
        when(meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)).thenReturn(true);
        when(meshMailBoxScheduler.isEnabled()).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of(MESSAGE_ID));
        when(meshClient.getEdifactMessage(any())).thenReturn(meshMessage);

        meshService.scanMeshInboxForMessages();

        verify(meshClient).getEdifactMessage(MESSAGE_ID);
        verify(inboundQueueService).publish(meshMessage);
        verify(meshClient).acknowledgeMessage(MESSAGE_ID);
    }

    @Test
    public void When_IntervalPassedAndRequestToGetMessageListFails_Then_DoNotPublishAndAcknowledgeMessages() {
        MeshService meshService = new MeshService(meshClient,
                inboundQueueService,
                meshMailBoxScheduler,
                scanDelayInSeconds);
        when(meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)).thenReturn(true);
        when(meshMailBoxScheduler.isEnabled()).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenThrow(new MeshApiConnectionException("error"));

        assertThatThrownBy(meshService::scanMeshInboxForMessages).isExactlyInstanceOf(MeshApiConnectionException.class);

        verify(meshClient, times(0)).getEdifactMessage(any());
        verify(inboundQueueService, times(0)).publish(any());
        verify(meshClient, times(0)).acknowledgeMessage(any());
    }

    @Test
    public void When_IntervalPassedAndRequestToDownloadMeshMessageFails_Then_DoNotPublishAndAcknowledgeMessage() {
        MeshService meshService = new MeshService(meshClient,
                inboundQueueService,
                meshMailBoxScheduler,
                scanDelayInSeconds);
        when(meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)).thenReturn(true);
        when(meshMailBoxScheduler.isEnabled()).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of(ERROR_MESSAGE_ID));
        when(meshClient.getEdifactMessage(any())).thenThrow(new MeshApiConnectionException("error"));

        meshService.scanMeshInboxForMessages();

        verify(meshClient).getEdifactMessage(ERROR_MESSAGE_ID);
        verify(inboundQueueService, times(0)).publish(any());
        verify(meshClient, times(0)).acknowledgeMessage(MESSAGE_ID);
    }

    @Test
    public void When_IntervalPassedAndRequestToDownloadMeshMessageFails_Then_SkipMessageAndDownloadNextOne() {
        MeshService meshService = new MeshService(meshClient,
                inboundQueueService,
                meshMailBoxScheduler,
                scanDelayInSeconds);


        when(meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)).thenReturn(true);
        when(meshMailBoxScheduler.isEnabled()).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of(ERROR_MESSAGE_ID, MESSAGE_ID));
        when(meshClient.getEdifactMessage(MESSAGE_ID)).thenThrow(new MeshApiConnectionException("error"));
        when(meshClient.getEdifactMessage(ERROR_MESSAGE_ID)).thenReturn(meshMessage);

        meshService.scanMeshInboxForMessages();

        verify(meshClient).getEdifactMessage(ERROR_MESSAGE_ID);
        verify(meshClient).getEdifactMessage(MESSAGE_ID);
        verify(inboundQueueService).publish(meshMessage);
        verify(meshClient).acknowledgeMessage(MESSAGE_ID);
    }

    @Test
    public void When_IntervalPassedAndPublishingToQueueFails_Then_DoNotAcknowledgeMessage() {
        MeshService meshService = new MeshService(meshClient,
                inboundQueueService,
                meshMailBoxScheduler,
                scanDelayInSeconds);
        when(meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)).thenReturn(true);
        when(meshMailBoxScheduler.isEnabled()).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of(MESSAGE_ID));
        when(meshClient.getEdifactMessage(any())).thenReturn(meshMessage);
        doThrow(new RuntimeException("error")).when(inboundQueueService).publish(any());

        meshService.scanMeshInboxForMessages();

        verify(meshClient).getEdifactMessage(MESSAGE_ID);
        verify(inboundQueueService).publish(meshMessage);
        verify(meshClient, times(0)).acknowledgeMessage(MESSAGE_ID);
    }

    @Test
    public void When_IntervalHasNotPassed_Then_DoNothing() {
        when(meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)).thenReturn(false);
        when(meshMailBoxScheduler.isEnabled()).thenReturn(true);

        meshService.scanMeshInboxForMessages();

        verifyNoInteractions(meshClient);
        verifyNoInteractions(inboundQueueService);
    }

    @Test
    public void When_IntervalHasPassedButNoMessagesFound_Then_DoNothing() {
        when(meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)).thenReturn(true);
        when(meshMailBoxScheduler.isEnabled()).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of());

        meshService.scanMeshInboxForMessages();

        verify(meshClient, times(0)).getEdifactMessage(MESSAGE_ID);
        verifyNoInteractions(inboundQueueService);
    }

    @Test
    public void When_SchedulerIsDisabled_Then_DoNothing() {
        when(meshMailBoxScheduler.isEnabled()).thenReturn(false);

        meshService.scanMeshInboxForMessages();

        verify(meshMailBoxScheduler, times(0)).hasTimePassed(scanDelayInSeconds);
        verify(meshClient, times(0)).getEdifactMessage(MESSAGE_ID);
        verifyNoInteractions(inboundQueueService);
    }

}