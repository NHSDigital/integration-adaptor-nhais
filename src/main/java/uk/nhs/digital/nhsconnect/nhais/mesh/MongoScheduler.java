package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.UpdateResult;

@Component
@Slf4j
public class MongoScheduler {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private MeshClient meshClient;

    private static final String MESH_TIMESTAMP_COLLECTION_NAME = "mesh_timestamp";
    private static final String TIMESTAMP_FIELD_NAME = "timestamp";

    @Scheduled(fixedRate = 6000)
    public void updateConditionally() {
        LOGGER.debug("Scheduled job for mesh messages fetching started");
        if (updateTimestamp()) {
            LOGGER.info("Mesh messages fetching started");
            for (String messageId : meshClient.getInboxMessageIds()) {
                meshClient.getEdifactMessage(messageId);
            }
        } else {
            LOGGER.info("Mesh messages fetching is postponed: another application instance is fetching now");
        }
    }

    private boolean updateTimestamp() {
        if (collectionIsNotEmpty()) {
            UpdateResult result = mongoOperations.updateFirst(query(where(TIMESTAMP_FIELD_NAME).lt(LocalDateTime.now().minusMinutes(5))),
                Update.update(TIMESTAMP_FIELD_NAME, LocalDateTime.now()),
                MESH_TIMESTAMP_COLLECTION_NAME);

            if (updateSuccessful(result)) {
                LOGGER.debug("Timestamp in {} collection has been set to current timestamp.", MESH_TIMESTAMP_COLLECTION_NAME);
                return true;
            } else {
                LOGGER.debug("Timestamp in {} collection is after five minutes ago, so it has not been modified", MESH_TIMESTAMP_COLLECTION_NAME);
                return false;
            }
        } else {
            LOGGER.info("{} collection does not exits or it is empty. Document with timestamp will be created", MESH_TIMESTAMP_COLLECTION_NAME);
            Document document = new Document();
            document.put(TIMESTAMP_FIELD_NAME, LocalDateTime.now());
            mongoOperations.save(document, MESH_TIMESTAMP_COLLECTION_NAME);
            return false;
        }
    }

    private boolean collectionIsNotEmpty() {
        return mongoOperations.collectionExists(MESH_TIMESTAMP_COLLECTION_NAME)
            && mongoOperations.getCollection(MESH_TIMESTAMP_COLLECTION_NAME).countDocuments() != 0;
    }

    private boolean updateSuccessful(UpdateResult result) {
        return result.getModifiedCount() == 1L;
    }
}
