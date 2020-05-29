package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class TimestampService {
    public Instant getCurrentTimestamp() {
        var now = Instant.now();
        return now.minusNanos(now.getNano());
    }

    public String getCurrentDateTimeInISOFormat() {
        return DateTimeFormatter.ISO_DATE_TIME
            .withZone(ZoneOffset.UTC)
            .format(getCurrentTimestamp());
    }
}
