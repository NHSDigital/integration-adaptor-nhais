package uk.nhs.digital.nhsconnect.nhais.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;

import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
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
    private MongoCollection mongoCollection;

    @Mock
    private TimestampService timestampService;

    @Test
    public void whenCollectionDoesNotExistCreateDocumentAndReturnFalse() {
        when(mongoOperations.getCollection(isA(String.class))).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(0L);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());

        boolean updated = schedulerTimestampRepositoryExtensions.updateTimestamp(SCHEDULER_TYPE, Instant.now(), 300);

        assertThat(updated).isFalse();
    }

    @Test
    public void whenUpdatedThenReturnTrue() {
        when(mongoOperations.getCollection(isA(String.class))).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(1L);

        when(mongoOperations.updateFirst(isA(Query.class), isA(UpdateDefinition.class), isA(String.class))).thenReturn(updateResult);
        when(updateResult.getModifiedCount()).thenReturn(1L);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());

        boolean updated = schedulerTimestampRepositoryExtensions.updateTimestamp(SCHEDULER_TYPE, Instant.now(), 300);

        assertThat(updated).isTrue();
    }

    @Test
    public void whenNotUpdatedThenReturnFalse() {
        when(mongoOperations.getCollection(isA(String.class))).thenReturn(mongoCollection);
        when(mongoCollection.countDocuments()).thenReturn(1L);

        when(mongoOperations.updateFirst(isA(Query.class), isA(UpdateDefinition.class), isA(String.class))).thenReturn(updateResult);
        when(updateResult.getModifiedCount()).thenReturn(0L);
        when(timestampService.getCurrentTimestamp()).thenReturn(Instant.now());

        boolean updated = schedulerTimestampRepositoryExtensions.updateTimestamp(SCHEDULER_TYPE, Instant.now(), 300);

        assertThat(updated).isFalse();
    }
}
