package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonGPPreviousTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PGP+4826940,281:900'";

        var personGPPrevious = PersonGPPrevious.builder()
            .identifier("4826940,281")
            .code("900")
            .build();

        assertEquals(expectedValue, personGPPrevious.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyFields_Then_EdifactValidationExceptionIsThrown() {
        var personGPPrevious = PersonGPPrevious.builder()
            .identifier("")
            .code("")
            .build();

        assertThrows(EdifactValidationException.class, personGPPrevious::toEdifact);
    }

    @Test
    public void When_BuildingWithoutMandatoryFields_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonGPPrevious.builder().build());
    }
}
