package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.repository.SchedulerTimestampRepositoryExtensions;

@ExtendWith(MockitoExtension.class)
public class MongoSchedulerTest {

    private static final String MESSAGE_ID = "messageId";
    private static final String EDIFACT_MESSAGE = "edifactMessage";

    @InjectMocks
    MongoScheduler mongoScheduler;

    @Mock
    private MeshClient meshClient;

    @Mock
    private SchedulerTimestampRepositoryExtensions schedulerTimestampRepository;

    @Test
    public void WhenCollectionIsEmptyThenSingleDocumentIsCreatedAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(isA(String.class), isA(LocalDateTime.class), anyLong())).thenReturn(false);
        mongoScheduler.updateConditionally();
        verifyNoInteractions(meshClient);
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsBeforeFiveMinutesAgoThenDocumentIsUpdateAndTheJobIsExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(isA(String.class), isA(LocalDateTime.class), anyLong())).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of(MESSAGE_ID));
        when(meshClient.getEdifactMessage(MESSAGE_ID)).thenReturn(EDIFACT_MESSAGE);
        mongoScheduler.updateConditionally();
        verify(meshClient).getEdifactMessage(any(String.class));
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsAfterFiveMinutesAgoThenDocumentIsNotUpdateAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(isA(String.class), isA(LocalDateTime.class), anyLong())).thenReturn(false);
        mongoScheduler.updateConditionally();
        verifyNoInteractions(meshClient);
    }
}
