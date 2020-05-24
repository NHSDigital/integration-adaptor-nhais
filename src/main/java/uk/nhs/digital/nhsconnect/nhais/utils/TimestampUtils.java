package uk.nhs.digital.nhsconnect.nhais.utils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;

public class TimestampUtils {

    public static ZonedDateTime getCurrentDateTimeAsUTC() {
        return ZonedDateTime.ofInstant(Instant.now(), UTC);
    }

    public static String getCurrentDateTimeInISOFormat() {
        return getCurrentDateTimeAsUTC().format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
