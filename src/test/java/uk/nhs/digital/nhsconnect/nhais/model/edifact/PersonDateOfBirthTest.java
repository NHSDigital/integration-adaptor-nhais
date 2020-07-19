package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonDateOfBirthTest {
    private static final LocalDate FIXED_TIME = LocalDate.of(1991, 11, 6);

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "DTM+329:19911106:102'";

        var personDob = PersonDateOfBirth.builder()
            .dateOfBirth(FIXED_TIME)
            .build();

        assertEquals(expectedValue, personDob.toEdifact());
    }

    @Test
    public void When_BuildingWithEmptyTimestamp_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonDateOfBirth.builder().build());
    }
}
