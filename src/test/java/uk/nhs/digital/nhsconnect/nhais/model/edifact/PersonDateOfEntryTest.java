package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonDateOfEntryTest {
    private static final Instant FIXED_TIME = ZonedDateTime.of(
            1992,
            1,
            13,
            23,
            55,
            0,
            0,
            ZoneId.of("Europe/London")).toInstant();


    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "DTM+957:19920113:102'";

        var personDateOfEntry = PersonDateOfEntry.builder()
                .timestamp(FIXED_TIME)
                .build();

        assertEquals(expectedValue, personDateOfEntry.toEdifact());
    }

    @Test
    public void When_BuildingWithEmptyTimestamp_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonDateOfEntry.builder().build());
    }
}
