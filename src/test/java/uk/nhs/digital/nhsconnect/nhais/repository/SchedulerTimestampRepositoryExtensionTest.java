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

    @Test
    public void whenUpdatedThenReturnTrue() {

        when(updateResult.getModifiedCount()).thenReturn(1L);

        when(mongoOperations.upsert(isA(Query.class), isA(UpdateDefinition.class), isA(String.class))).thenReturn(updateResult);

        boolean updated = schedulerTimestampRepositoryExtensions.updateTimestamp(SCHEDULER_TYPE, LocalDateTime.now(), 300);

        assertThat(updated).isTrue();
    }

    @Test
    public void whenNotUpdatedThenReturnFalse() {

        when(updateResult.getModifiedCount()).thenReturn(0L);

        when(mongoOperations.upsert(isA(Query.class), isA(UpdateDefinition.class), isA(String.class))).thenReturn(updateResult);

        boolean updated = schedulerTimestampRepositoryExtensions.updateTimestamp(SCHEDULER_TYPE, LocalDateTime.now(), 300);

        assertThat(updated).isFalse();
    }


}
