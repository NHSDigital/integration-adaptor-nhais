package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeductionReasonCodeTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "GIS+11:ZZZ'";

        var deductionReasonCode = DeductionReasonCode.builder()
            .code("11")
            .build();

        assertEquals(expectedValue, deductionReasonCode.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyType_Then_EdifactValidationExceptionIsThrown() {
        var deductionReasonCode = DeductionReasonCode.builder()
            .code("")
            .build();

        assertThrows(EdifactValidationException.class, deductionReasonCode::toEdifact);
    }

    @Test
    public void When_BuildingWithoutType_Then_IsThrown() {
        assertThrows(NullPointerException.class, () -> DeductionReasonCode.builder().build());
    }
}
