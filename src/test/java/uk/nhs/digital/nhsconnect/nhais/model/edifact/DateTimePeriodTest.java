package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimePeriodTest {

    private final Instant translationDateTime = ZonedDateTime
        .of(2020, 4, 28, 20, 58, 0, 0, TimestampService.UKZone)
        .toInstant();

    @Test
    public void testValidDateTimePeriod() throws EdifactValidationException {
        DateTimePeriod dateTimePeriod = new DateTimePeriod(translationDateTime, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);

        String edifact = dateTimePeriod.toEdifact();

        assertThat(edifact).isEqualTo("DTM+137:202004282058:203'");
    }
}