package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@CompoundIndexes({
    @CompoundIndex(
        name = "unique_document",
        def = "{'schedulerType': 1}",
        unique = true)
})
@Data
@Document
public class SchedulerTimestamp {
    private final String schedulerType;
    private final Instant updateTimestamp;
}
