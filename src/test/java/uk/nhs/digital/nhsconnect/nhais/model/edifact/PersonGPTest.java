package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonGPTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+GP+4826940,281:900'";

        var personGP = PersonGP.builder()
                .practitioner("4826940")
                .build();

        assertEquals(expectedValue, personGP.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyPractitioner_Then_EdifactValidationExceptionIsThrown() {
        var personGP = PersonGP.builder()
                .practitioner("")
                .build();

        assertThrows(EdifactValidationException.class, personGP::toEdifact);
    }

    @Test
    public void When_BuildingWithoutPractitioner_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonGP.builder().build());
    }
}
