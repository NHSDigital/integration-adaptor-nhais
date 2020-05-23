package uk.nhs.digital.nhsconnect.nhais.utils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;

public class TimestampUtils {

    public static String getCurrentDateTimeInISOFormat() {
        return ZonedDateTime.ofInstant(Instant.now(), UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
