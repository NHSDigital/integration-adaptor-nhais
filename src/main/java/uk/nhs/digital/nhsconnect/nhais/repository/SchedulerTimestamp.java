package uk.nhs.digital.nhsconnect.nhais.repository;

import java.time.Instant;

import lombok.Data;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_document",
        def = "{'timestamp' : 1, 'schedulerType': 1}",
        unique = true)
})
@Data
@Document
public class SchedulerTimestamp {
    private String schedulerType;
    private Instant timestamp;
}
