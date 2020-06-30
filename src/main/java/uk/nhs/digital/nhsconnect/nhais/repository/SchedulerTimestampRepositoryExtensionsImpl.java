package uk.nhs.digital.nhsconnect.nhais.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.DuplicateKeyException;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SchedulerTimestampRepositoryExtensionsImpl implements SchedulerTimestampRepositoryExtensions {

    private static final String MESH_TIMESTAMP_COLLECTION_NAME = "schedulerTimestamp";
    private static final String TIMESTAMP = "timestamp";
    private static final String SCHEDULER_TYPE = "schedulerType";

    private final MongoOperations mongoOperations;

    @Override
    public boolean updateTimestamp(String schedulerType, LocalDateTime timestamp, long seconds) {
        var query =  query(where(TIMESTAMP).lt(LocalDateTime.now().minusSeconds(seconds))
            .and(SCHEDULER_TYPE).is(schedulerType));

        var update = Update.update(TIMESTAMP, timestamp)
            .set(SCHEDULER_TYPE, schedulerType);

        var result = mongoOperations.upsert(query, update, MESH_TIMESTAMP_COLLECTION_NAME);

        return result.getModifiedCount() == 1L;
    }

}
