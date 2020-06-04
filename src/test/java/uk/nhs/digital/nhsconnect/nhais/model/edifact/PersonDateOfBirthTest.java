package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonDateOfBirthTest {
    private static final Instant FIXED_TIME = ZonedDateTime.of(
            1991,
            11,
            6,
            23,
            55,
            0,
            0,
            ZoneId.of("Europe/London")).toInstant();


    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "DTM+329:19911106:102'";

        var personDob = PersonDateOfBirth.builder()
                .timestamp(FIXED_TIME)
                .build();

        assertEquals(expectedValue, personDob.toEdifact());
    }

    @Test
    public void When_BuildingWithEmptyTimestamp_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonDateOfBirth.builder().build());
    }
}
