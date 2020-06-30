package uk.nhs.digital.nhsconnect.nhais.mesh;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.repository.SchedulerTimestampRepositoryExtensions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MongoScheduler {

    private final MongoOperations mongoOperations;
    private final MeshClient meshClient;
    private final SchedulerTimestampRepositoryExtensions schedulerTimestampRepository;

    @Value("${nhais.scheduler.interval}")
    private long minutes;

    private static final String SCHEDULER_TYPE = "meshTimestamp";
    private static final String MESH_TIMESTAMP_COLLECTION_NAME = "schedulerTimestamp";

    @Scheduled(fixedRate = 6000)
    public void updateConditionally() {
        LOGGER.debug("Scheduled job for mesh messages fetching started");
        if (updateTimestamp()) {
            LOGGER.debug("Timestamp in {} collection has been set to current timestamp.", MESH_TIMESTAMP_COLLECTION_NAME);
            LOGGER.info("Mesh messages fetching started");

            for (String messageId : meshClient.getInboxMessageIds()) {
                meshClient.getMessage(messageId);
            }
        } else {
            LOGGER.debug("Timestamp in {} collection is after five minutes ago, so it has not been modified", MESH_TIMESTAMP_COLLECTION_NAME);
            LOGGER.info("Mesh messages fetching is postponed: another application instance is fetching now");
        }
    }

    private boolean updateTimestamp() {
            var queryParams = new SchedulerTimestampRepositoryExtensions.UpdateTimestampParams(
                SCHEDULER_TYPE, minutes);

            var timestampDetails = new SchedulerTimestampRepositoryExtensions.UpdateTimestampDetails(
                SCHEDULER_TYPE, LocalDateTime.now());
            return schedulerTimestampRepository.updateTimestamp(queryParams, timestampDetails);

    }
}
