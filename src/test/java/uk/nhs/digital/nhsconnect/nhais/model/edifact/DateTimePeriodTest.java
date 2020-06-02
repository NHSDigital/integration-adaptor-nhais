package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimePeriodTest {

    private final Instant TRANSLATION_WINTER_DATE_TIME = ZonedDateTime
        .of(2020, 3, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final Instant TRANSLATION_SUMMER_DATE_TIME = ZonedDateTime
        .of(2020, 5, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();

    @Test
    public void testValidWinterDateTimePeriod() throws EdifactValidationException {
        DateTimePeriod dateTimePeriod = new DateTimePeriod(TRANSLATION_WINTER_DATE_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);

        String edifact = dateTimePeriod.toEdifact();

        assertThat(edifact).isEqualTo("DTM+137:202003282058:203'");
    }

    @Test
    public void testValidSummerDateTimePeriod() throws EdifactValidationException {
        DateTimePeriod dateTimePeriod = new DateTimePeriod(TRANSLATION_SUMMER_DATE_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);

        String edifact = dateTimePeriod.toEdifact();

        assertThat(edifact).isEqualTo("DTM+137:202005282158:203'");
    }
}