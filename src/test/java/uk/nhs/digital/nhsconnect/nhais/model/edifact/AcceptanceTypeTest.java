package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AcceptanceTypeTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "HEA+ATP+1:ZZZ'";

        var acceptanceType = AcceptanceType.builder()
                .type("1")
                .build();

        assertEquals(expectedValue, acceptanceType.toEdifact());
    }

    @Test
    public void When_MappingWithWrongType_Then_EdifactValidationExceptionIsThrown() {
        var acceptanceType = AcceptanceType.builder()
                .type("a")
                .build();

        assertThrows(EdifactValidationException.class, acceptanceType::toEdifact);
    }

    @Test
    public void When_MappingToEdifactWithEmptyType_Then_EdifactValidationExceptionIsThrown() {
        var acceptanceType = AcceptanceType.builder()
                .type("")
                .build();

        assertThrows(EdifactValidationException.class, acceptanceType::toEdifact);
    }

    @Test
    public void When_BuildingWithoutType_Then_IsThrown() {
        assertThrows(NullPointerException.class, () -> AcceptanceType.builder().build());
    }
}
