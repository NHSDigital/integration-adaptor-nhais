package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreviousGpNameTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+PGP+++DR PREVIOUS'";

        var personGPPrevious = PreviousGpName.builder()
            .partyName("DR PREVIOUS")
            .build();

        assertEquals(expectedValue, personGPPrevious.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyFields_Then_EdifactValidationExceptionIsThrown() {
        var personGPPrevious = PreviousGpName.builder()
            .partyName("")
            .build();

        assertThrows(EdifactValidationException.class, personGPPrevious::toEdifact);
    }

    @Test
    public void When_BuildingWithoutMandatoryFields_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PreviousGpName.builder().build());
    }
}
