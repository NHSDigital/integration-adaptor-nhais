package uk.nhs.digital.nhsconnect.nhais.repository;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface SchedulerTimestampRepositoryExtentions {
    boolean updateTimestamp(
        SchedulerTimestampRepositoryExtentions.UpdateTimestampParams updateTimestampParams,
        SchedulerTimestampRepositoryExtentions.UpdateTimestampDetails updateTimestampDetails);

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
