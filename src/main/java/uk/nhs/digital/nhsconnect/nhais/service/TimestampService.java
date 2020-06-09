package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimestampService {
    public static final ZoneId UKZone = ZoneId.of("Europe/London");

    public Instant getCurrentTimestamp() {
        var now = Instant.now();
        return now.minusNanos(now.getNano());
    }

    public String formatInISO(Instant timestamp) {
        return DateTimeFormatter.ISO_DATE_TIME
            .withZone(TimestampService.UKZone)
            .format(timestamp);
    }

    public Instant parseFromISO(String timestamp) {
        return ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME.withZone(TimestampService.UKZone))
            .toInstant();
    }
}
