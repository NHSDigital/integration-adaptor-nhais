package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DateTimePeriodTest {

    private final Instant TRANSLATION_WINTER_DATE_TIME = ZonedDateTime
        .of(2020, 3, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final DateTimePeriod winterDateTimePeriod = new DateTimePeriod(TRANSLATION_WINTER_DATE_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);
    private final Instant TRANSLATION_SUMMER_DATE_TIME = ZonedDateTime
        .of(2020, 5, 28, 20, 58, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final DateTimePeriod summerDateTimePeriod = new DateTimePeriod(TRANSLATION_SUMMER_DATE_TIME, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);

    @Test
    public void testValidWinterDateTimePeriod() throws EdifactValidationException {
        String edifact = winterDateTimePeriod.toEdifact();

        assertThat(edifact).isEqualTo("DTM+137:202003282058:203'");
    }

    @Test
    public void testValidSummerDateTimePeriod() throws EdifactValidationException {
        String edifact = summerDateTimePeriod.toEdifact();

        assertThat(edifact).isEqualTo("DTM+137:202005282158:203'");
    }

    @Test
    void testFromString() {
        assertThat(DateTimePeriod.fromString("DTM+137:202005282158:203").getValue()).isEqualTo(summerDateTimePeriod.getValue());
        assertThatThrownBy(() -> DateTimePeriod.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void When_acceptanceDateType_Then_edifactFormattedAsAcceptanceDate() {
        DateTimePeriod dateTimePeriod = new DateTimePeriod(LocalDate.parse("1992-01-15").atStartOfDay(ZoneOffset.UTC).toInstant(), DateTimePeriod.TypeAndFormat.ACCEPTANCE_DATE);
        assertThat(dateTimePeriod.toEdifact()).isEqualTo("DTM+956:19920115:102'");
    }

}