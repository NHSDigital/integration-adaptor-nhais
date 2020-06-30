package uk.nhs.digital.nhsconnect.nhais.repository;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface SchedulerTimestampRepositoryExtensions {
    boolean updateTimestamp(
        SchedulerTimestampRepositoryExtensions.UpdateTimestampParams updateTimestampParams,
        SchedulerTimestampRepositoryExtensions.UpdateTimestampDetails updateTimestampDetails);

    @RequiredArgsConstructor
    @Getter
    @ToString
    class UpdateTimestampParams {
        private final String schedulerType;
        private final long minutes;
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    class UpdateTimestampDetails {
        private final String schedulerType;
        private final LocalDateTime timestamp;
    }

}
