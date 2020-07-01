package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.repository.SchedulerTimestampRepository;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

@ExtendWith(MockitoExtension.class)
public class MongoSchedulerTest {

    private static final String MESSAGE_ID = "messageId";
    private static final String EDIFACT_MESSAGE = "edifactMessage";

    @InjectMocks
    MongoScheduler mongoScheduler;

    @Mock
    private MeshClient meshClient;

    @Mock
    private SchedulerTimestampRepository schedulerTimestampRepository;

    @Mock
    private TimestampService timestampService;

    @Test
    public void WhenCollectionIsEmptyThenSingleDocumentIsCreatedAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(anyString(), isA(Instant.class), anyLong())).thenReturn(false);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());
        mongoScheduler.updateConditionally();
        verifyNoInteractions(meshClient);
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsBeforeFiveMinutesAgoThenDocumentIsUpdateAndTheJobIsExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(anyString(), isA(Instant.class), anyLong())).thenReturn(true);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());
        when(meshClient.getInboxMessageIds()).thenReturn(List.of(MESSAGE_ID));
        when(meshClient.getEdifactMessage(MESSAGE_ID)).thenReturn(EDIFACT_MESSAGE);
        mongoScheduler.updateConditionally();
        verify(meshClient).getEdifactMessage(any(String.class));
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsAfterFiveMinutesAgoThenDocumentIsNotUpdateAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(anyString(), isA(Instant.class), anyLong())).thenReturn(false);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());
        mongoScheduler.updateConditionally();
        verifyNoInteractions(meshClient);
    }
}
