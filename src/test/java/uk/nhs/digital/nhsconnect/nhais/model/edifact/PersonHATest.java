package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonHATest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+FHS+XX1:954'";

        var personHA = PersonHA.builder()
            .organization("XX1")
            .build();

        assertEquals(expectedValue, personHA.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyHA_Then_EdifactValidationExceptionIsThrown() {
        var personHA = PersonHA.builder()
            .organization("")
            .build();

        assertThrows(EdifactValidationException.class, personHA::toEdifact);
    }

    @Test
    public void When_BuildingWithoutOrganization_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonHA.builder().build());
    }
}
