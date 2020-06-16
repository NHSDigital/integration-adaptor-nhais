package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PartyQualifierTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+FHS+XX1:954'";

        var partyQualifier = PartyQualifier.builder()
            .organization("XX1")
            .build();

        assertEquals(expectedValue, partyQualifier.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyHA_Then_EdifactValidationExceptionIsThrown() {
        var partyQualifier = PartyQualifier.builder()
            .organization("")
            .build();

        assertThrows(EdifactValidationException.class, partyQualifier::toEdifact);
    }

    @Test
    public void When_BuildingWithoutOrganization_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PartyQualifier.builder().build());
    }
}
