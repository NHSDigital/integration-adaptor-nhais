package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonNameTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() throws EdifactValidationException {
        var expectedValue = "PNA+PAT+1234567890:OPI+++SU:STEVENS+FO:CHARLES+TI:MR+MI:ANTHONY+FS:JOHN'";

        var personName = PersonName.builder()
            .nhsNumber("1234567890")
            .patientIdentificationType("OPI")
            .familyName("STEVENS")
            .forename("CHARLES")
            .title("MR")
            .middleName("ANTHONY")
            .thirdForename("JOHN")
            .build();

        assertEquals(expectedValue, personName.toEdifact());
    }

    @Test
    public void When_BuildingEmptyName_Then_ReturnEmptySegment() {
        var expectedValue = "PNA+PAT+'";

        var personName = PersonName.builder()
            .build();

        assertEquals(expectedValue, personName.toEdifact());
    }
}
