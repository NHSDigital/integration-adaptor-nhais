package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.EdifactToMeshMessageService;
import uk.nhs.digital.nhsconnect.nhais.service.InboundQueueService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeshServiceTest {

    @Mock
    private MeshClient meshClient;

    @Mock
    private EdifactToMeshMessageService edifactToMeshMessageService;

    @Mock
    private InboundQueueService inboundQueueService;

    @Mock
    private MeshMailBoxScheduler meshMailBoxScheduler;

    private long intervalInSeconds = 5L;
    private static final String MESSAGE_ID = "messageId";
    private MeshService meshService;
    private MeshMessage meshMessage;

    @BeforeEach
    public void setUp() {
        meshMessage = new MeshMessage();
        meshMessage.setMeshMessageId(MESSAGE_ID);

        meshService = new MeshService(meshClient,
                edifactToMeshMessageService,
                inboundQueueService,
                meshMailBoxScheduler,
                intervalInSeconds);
    }

    @Test
    public void When_IntervalPassedAndMessagesFound_Then_DownloadAndPublishMessage() {
        MeshService meshService = new MeshService(meshClient,
                edifactToMeshMessageService,
                inboundQueueService,
                meshMailBoxScheduler,
                intervalInSeconds);
        when(meshMailBoxScheduler.hasTimePassed(intervalInSeconds)).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of(MESSAGE_ID));
        when(edifactToMeshMessageService.fromEdifactString(any(), any())).thenReturn(meshMessage);

        meshService.scanMeshInboxForMessages();

        verify(meshClient).getEdifactMessage(MESSAGE_ID);
        verify(inboundQueueService).publish(any());
        verify(meshClient).acknowledgeMessage(MESSAGE_ID);
    }

    @Test
    public void When_IntervalHasNotPassed_Then_DoNothing() {
        when(meshMailBoxScheduler.hasTimePassed(intervalInSeconds)).thenReturn(false);

        meshService.scanMeshInboxForMessages();

        verifyNoInteractions(meshClient);
        verifyNoInteractions(inboundQueueService);
    }

    @Test
    public void When_IntervalHasPassedButNoMessagesFound_Then_DoNothing() {
        when(meshMailBoxScheduler.hasTimePassed(intervalInSeconds)).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of());

        meshService.scanMeshInboxForMessages();

        verify(meshClient, times(0)).getEdifactMessage(MESSAGE_ID);
        verifyNoInteractions(inboundQueueService);
    }

}