package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.repository.SchedulerTimestampRepositoryExtensions;

@ExtendWith(MockitoExtension.class)
public class MongoSchedulerTest {

    private static final String MESH_TIMESTAMP = "mesh_timestamp";

    @InjectMocks
    MongoScheduler mongoScheduler;

    @Mock
    private MeshClient meshClient;

    @Mock
    private SchedulerTimestampRepositoryExtensions schedulerTimestampRepository;

    @Test
    public void WhenCollectionIsEmptyThenSingleDocumentIsCreatedAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(any(), any())).thenReturn(false);
        mongoScheduler.updateConditionally();

        verifyNoInteractions(meshClient);
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsBeforeFiveMinutesAgoThenDocumentIsUpdateAndTheJobIsExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(any(), any())).thenReturn(true);
        when(meshClient.getInboxMessageIds()).thenReturn(List.of("messageId"));
        when(meshClient.getEdifactMessage("messageId")).thenReturn("something");


        mongoScheduler.updateConditionally();

        verify(meshClient).getEdifactMessage(any(String.class));
    }

    @Test
    public void WhenDocumentExistsAndTimestampIsAfterFiveMinutesAgoThenDocumentIsNotUpdateAndTheJobIsNotExecuted() {
        when(schedulerTimestampRepository.updateTimestamp(any(), any())).thenReturn(false);
        mongoScheduler.updateConditionally();
        verifyNoInteractions(meshClient);
    }
}
