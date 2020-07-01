package uk.nhs.digital.nhsconnect.nhais.repository;

import java.time.Instant;

public interface SchedulerTimestampRepositoryExtensions {

    boolean updateTimestamp(String schedulerType, Instant timestamp, long minutes);
}
