package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.InboundQueueService;

import java.util.List;

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
        verify(inboundQueueService).publish(any());
        verify(meshClient).acknowledgeMessage(MESSAGE_ID);
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