package uk.nhs.digital.nhsconnect.nhais.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import com.mongodb.client.result.UpdateResult;

@ExtendWith(MockitoExtension.class)
public class SchedulerTimestampRepositoryExtensionTest {

    private static final String SCHEDULER_TYPE = "meshTimestamp";

    @InjectMocks
    SchedulerTimestampRepositoryExtensionsImpl schedulerTimestampRepositoryExtensions;

    @Mock
    private MongoOperations mongoOperations;

    @Mock
    private UpdateResult updateResult;

    @Mock
    private SchedulerTimestampRepositoryExtensions.UpdateTimestampParams updateTimestampParams;

    @Mock
    private SchedulerTimestampRepositoryExtensions.UpdateTimestampDetails updateTimestampDetails;

    @Test
    public void whenUpdatedThenReturnTrue() {
        when(updateTimestampParams.getMinutes()).thenReturn(5L);
        when(updateTimestampParams.getSchedulerType()).thenReturn(SCHEDULER_TYPE);

        when(updateTimestampDetails.getSchedulerType()).thenReturn(SCHEDULER_TYPE);
        when(updateTimestampDetails.getTimestamp()).thenReturn(LocalDateTime.now().minusMinutes(6));

        when(updateResult.getModifiedCount()).thenReturn(1L);

        when(mongoOperations.upsert(isA(Query.class), isA(UpdateDefinition.class), isA(String.class))).thenReturn(updateResult);

        boolean updated = schedulerTimestampRepositoryExtensions.updateTimestamp(updateTimestampParams, updateTimestampDetails);

        assertThat(updated).isTrue();
    }

    @Test
    public void whenNotUpdatedThenReturnFalse() {
        when(updateTimestampParams.getMinutes()).thenReturn(5L);
        when(updateTimestampParams.getSchedulerType()).thenReturn(SCHEDULER_TYPE);

        when(updateTimestampDetails.getSchedulerType()).thenReturn(SCHEDULER_TYPE);
        when(updateTimestampDetails.getTimestamp()).thenReturn(LocalDateTime.now().minusMinutes(3));

        when(updateResult.getModifiedCount()).thenReturn(0L);

        when(mongoOperations.upsert(isA(Query.class), isA(UpdateDefinition.class), isA(String.class))).thenReturn(updateResult);

        boolean updated = schedulerTimestampRepositoryExtensions.updateTimestamp(updateTimestampParams, updateTimestampDetails);

        assertThat(updated).isFalse();
    }


}
