package uk.nhs.digital.nhsconnect.nhais.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SchedulerTimestampRepositoryExtensionsImpl implements SchedulerTimestampRepositoryExtensions {

    private final MongoOperations mongoOperations;

    @Override
    public boolean updateTimestamp(UpdateTimestampParams updateTimestampParams, UpdateTimestampDetails updateTimestampDetails) {
        var query =  query(where("timestamp").lt(LocalDateTime.now().minusMinutes(updateTimestampParams.getMinutes()))
            .and("schedulerType").is(updateTimestampParams.getSchedulerType()));

        var update = Update.update("timestamp", LocalDateTime.now()).set("schedulerType", "meshTimestamp");

        var result = mongoOperations.upsert(query,
            update, "meshTimestamp");

        return result.getModifiedCount() == 1L;
    }

}
