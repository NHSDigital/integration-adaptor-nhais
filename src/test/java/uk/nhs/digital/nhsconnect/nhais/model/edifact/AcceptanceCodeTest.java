package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AcceptanceCodeTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "HEA+ACD+A:ZZZ'";

        var acceptanceCode = AcceptanceCode.builder()
                .code("A")
                .build();

        assertEquals(expectedValue, acceptanceCode.toEdifact());
    }

    @Test
    public void When_MappingWithWrongCode_Then_EdifactValidationExceptionIsThrown() {
        var acceptanceCode = AcceptanceCode.builder()
                .code("B")
                .build();

        assertThrows(EdifactValidationException.class, acceptanceCode::toEdifact);
    }

    @Test
    public void When_MappingToEdifactWithEmptyType_Then_EdifactValidationExceptionIsThrown() {
        var acceptanceCode = AcceptanceCode.builder()
                .code("")
                .build();

        assertThrows(EdifactValidationException.class, acceptanceCode::toEdifact);
    }

    @Test
    public void When_BuildingWithoutType_Then_IsThrown() {
        assertThrows(NullPointerException.class, () -> AcceptanceCode.builder().build());
    }
}
