package uk.nhs.digital.nhsconnect.nhais.repository;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface SchedulerTimestampRepositoryExtensions {

    boolean updateTimestamp(String schedulerType, LocalDateTime timestamp, long minutes);
}
