package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AcceptanceTypeTest {

    @Test
    public void When_MappingToEdifact_Expect_ReturnCorrectString() {
        var expectedValue = "HEA+ATP+1:ZZZ'";

        var acceptanceType = AcceptanceType.builder()
            .acceptanceType(AcceptanceType.AvailableTypes.BIRTH)
            .build();

        assertEquals(expectedValue, acceptanceType.toEdifact());
    }

    @Test
    public void When_BuildingWithoutType_Expect_IsThrown() {
        assertThrows(NullPointerException.class, () -> AcceptanceType.builder().build());
    }
}
