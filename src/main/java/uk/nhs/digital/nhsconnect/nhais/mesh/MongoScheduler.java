package uk.nhs.digital.nhsconnect.nhais.mesh;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.LocalDateTime;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.UpdateResult;

@Component
public class MongoScheduler {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private MeshClient meshClient;

    private static final String MESH_TIMESTAMP = "mesh_timestamp";
    private static final String TIMESTAMP = "timestamp";

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoScheduler.class);

    @Scheduled(fixedRate = 60000)
    public void updateConditionally() {
        LOGGER.info("Scheduled job for mesh messages fetching started");
        if (checkTheTimestampUpdatability()) {
            LOGGER.info("Mesh messages fetching started");
            for (String messageId : meshClient.getInboxMessageIds()) {
                meshClient.getMessage(messageId);
            }
        } else {
            LOGGER.info("Mesh messages fetching is postponed: another application instance is fetching now");
        }
    }

    private boolean checkTheTimestampUpdatability() {
        if (mongoOperations.collectionExists(MESH_TIMESTAMP) && mongoOperations.getCollection(MESH_TIMESTAMP).countDocuments() != 0) {
            UpdateResult result = mongoOperations.updateFirst(query(where(TIMESTAMP).lt(LocalDateTime.now().minusMinutes(5))),
                Update.update(TIMESTAMP, LocalDateTime.now()),
                MESH_TIMESTAMP);

            if (result.getModifiedCount() == 1L) {
                LOGGER.info("Timestamp in mesh_timestamp collection has been set to current timestamp.");
                return true;
            } else {
                LOGGER.info("Timestamp in mesh_timestamp collection is after five minutes ago, so it has not been modified");
                return false;
            }
        } else {
            LOGGER.info("mesh_timestamp collection does not exits or it is empty. Document with timestamp will be created");
            Document document = new Document();
            document.put(TIMESTAMP, LocalDateTime.now());
            mongoOperations.save(document, MESH_TIMESTAMP);
            mongoOperations.getCollection(MESH_TIMESTAMP).createIndex(document, new IndexOptions().unique(true));
            return false;
        }
    }
}
