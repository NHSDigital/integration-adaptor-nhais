package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonGPPreviousTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PGP+4826940,281:900'";

        var personGPPrevious = PersonGPPrevious.builder()
                .practitioner("4826940")
                .build();

        assertEquals(expectedValue, personGPPrevious.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyPractitioner_Then_EdifactValidationExceptionIsThrown() {
        var personGPPrevious = PersonGPPrevious.builder()
                .practitioner("")
                .build();

        assertThrows(EdifactValidationException.class, personGPPrevious::toEdifact);
    }

    @Test
    public void When_BuildingWithoutPractitioner_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonGPPrevious.builder().build());
    }
}
