package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateTimePeriodTest {

    private final ZonedDateTime translationDateTime = ZonedDateTime.of(2020, 4, 28, 20, 58, 0, 0, ZoneOffset.UTC);

    @Test
    public void testValidDateTimePeriod() throws EdifactValidationException {
        DateTimePeriod dateTimePeriod = new DateTimePeriod(translationDateTime,DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);

        String edifact = dateTimePeriod.toEdifact();

        assertEquals("DTM+137:202004282058:203'", edifact);
    }

    @Test
    public void whenParsingEdifactWithNewlines_thenSegmentParsedCorrectly() {
        DateTimePeriod expected = null; //new DateTimePeriod();
        DateTimePeriod dateTimePeriod = DateTimePeriod.fromEdifact(EdifactFixtures.STATE_ONLY_WITH_NEWLINES);
        assertEquals(expected, dateTimePeriod);
    }

    @Test
    public void whenParsingEdifactWithoutNewlines_thenSegmentParsedCorrectly() {
        BeginningOfMessage expected = new BeginningOfMessage();
        BeginningOfMessage beginningOfMessage = BeginningOfMessage.fromEdifact(EdifactFixtures.STATE_ONLY_WITHOUT_NEWLINES);
        assertEquals(expected, beginningOfMessage);
    }

    @Test
    public void whenParsingEdifactSegmentDoesNotExist_thenThrowsEdifactValidationException() {
        assertThrows(EdifactValidationException.class, () -> BeginningOfMessage.fromEdifact("asdf"));
    }
}