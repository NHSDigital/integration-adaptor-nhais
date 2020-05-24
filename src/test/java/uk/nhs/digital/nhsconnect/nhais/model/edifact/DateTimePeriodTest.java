package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimePeriodTest {

    private final ZonedDateTime translationDateTime = ZonedDateTime.of(2020, 4, 28, 20, 58, 0, 0, ZoneOffset.UTC);

    @Test
    public void testValidDateTimePeriod() throws EdifactValidationException {
        DateTimePeriod dateTimePeriod = new DateTimePeriod(translationDateTime,DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);

        String edifact = dateTimePeriod.toEdifact();

        assertEquals("DTM+137:202004282058:203'", edifact);
    }
}
