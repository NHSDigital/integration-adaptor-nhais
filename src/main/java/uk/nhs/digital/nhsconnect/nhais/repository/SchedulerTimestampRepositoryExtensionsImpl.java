package uk.nhs.digital.nhsconnect.nhais.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.Instant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SchedulerTimestampRepositoryExtensionsImpl implements SchedulerTimestampRepositoryExtensions {

    private static final String MESH_TIMESTAMP_COLLECTION_NAME = "schedulerTimestamp";
    private static final String SCHEDULER_TYPE = "schedulerType";
    private static final String TIMESTAMP_FIELD_NAME = "updateTimestamp";

    private final MongoOperations mongoOperations;
    private final TimestampService timestampService;

    @Override
    public boolean updateTimestamp(String schedulerType, Instant timestamp, long seconds) {
        var query = query(where(TIMESTAMP_FIELD_NAME).lt(timestampService.getCurrentTimestamp().minusSeconds(seconds))
            .and(SCHEDULER_TYPE).is(schedulerType));

        var update = Update.update(TIMESTAMP_FIELD_NAME, timestamp)
            .set(SCHEDULER_TYPE, schedulerType);

      if (collectionIsNotEmpty()) {
          UpdateResult result = mongoOperations.updateFirst(query, update, MESH_TIMESTAMP_COLLECTION_NAME);

          return updateSuccessful(result);
        } else {
            LOGGER.info("{} collection does not exist or it is empty. Document with timestamp will be created", MESH_TIMESTAMP_COLLECTION_NAME);
            SchedulerTimestamp schedulerTimestamp = new SchedulerTimestamp(schedulerType, timestampService.getCurrentTimestamp());
            mongoOperations.save(schedulerTimestamp, MESH_TIMESTAMP_COLLECTION_NAME);
            return false;
        }

    }

    private boolean collectionIsNotEmpty() {
        return  mongoOperations.getCollection(MESH_TIMESTAMP_COLLECTION_NAME).countDocuments() != 0;
    }

    private boolean updateSuccessful(UpdateResult result) {
        return result.getModifiedCount() == 1L;
    }
}
