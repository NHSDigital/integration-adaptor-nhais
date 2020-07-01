package uk.nhs.digital.nhsconnect.nhais.mesh;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.repository.SchedulerTimestampRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.SchedulerTimestampRepositoryExtensions;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MongoScheduler {

    private final MeshClient meshClient;
    private final SchedulerTimestampRepository schedulerTimestampRepository;
    private final TimestampService timestampService;

    @Value("${nhais.scheduler.overlapIntervalInSeconds}")
    private long seconds;

    private static final String SCHEDULER_TYPE = "meshTimestamp";
    private static final String MESH_TIMESTAMP_COLLECTION_NAME = "schedulerTimestamp";

    @Scheduled(fixedRateString = "${nhais.scheduler.intervalInMilliSeconds}")
    public void updateConditionally() {
        LOGGER.debug("Scheduled job for mesh messages fetching started");
        if (updateTimestamp()) {
            LOGGER.debug("Timestamp in {} collection is less than {} seconds in the past, so it has not been modified", MESH_TIMESTAMP_COLLECTION_NAME, seconds);
            LOGGER.info("Mesh messages fetching started");

            for (String messageId : meshClient.getInboxMessageIds()) {
                meshClient.getEdifactMessage(messageId);
            }
        } else {
            LOGGER.debug("Timestamp in {} collection is after {} seconds ago, so it has not been modified", MESH_TIMESTAMP_COLLECTION_NAME, seconds);
            LOGGER.info("Mesh messages fetching is postponed: another application instance is fetching now");
        }
    }

    private boolean updateTimestamp() {
            return schedulerTimestampRepository.updateTimestamp(SCHEDULER_TYPE, timestampService.getCurrentTimestamp(), seconds);

    }
}
