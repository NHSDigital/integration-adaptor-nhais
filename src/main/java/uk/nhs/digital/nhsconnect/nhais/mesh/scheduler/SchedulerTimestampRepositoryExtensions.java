package uk.nhs.digital.nhsconnect.nhais.mesh.scheduler;

import java.time.Instant;

public interface SchedulerTimestampRepositoryExtensions {

    boolean updateTimestamp(String schedulerType, Instant timestamp, long seconds);
}
