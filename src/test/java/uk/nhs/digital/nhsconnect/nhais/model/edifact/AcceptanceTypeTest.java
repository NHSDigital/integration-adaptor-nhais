package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcceptanceTypeTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "HEA+ATP+1:ZZZ'";

        var acceptanceType = AcceptanceType.builder()
                .type("1")
                .build();

        assertEquals(expectedValue, acceptanceType.toEdifact());
    }
}
