package uk.nhs.digital.nhsconnect.nhais.scheduler;

import java.time.Instant;

public interface SchedulerTimestampRepositoryExtensions {

    boolean updateTimestamp(String schedulerType, Instant timestamp, long seconds);
}
