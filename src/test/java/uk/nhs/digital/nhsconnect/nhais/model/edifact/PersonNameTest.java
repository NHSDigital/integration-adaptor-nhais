package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonNameTest {

    @Test
    public void whenToEdifact_thenReturnSegmentString() throws EdifactValidationException {
        var expectedValue = "PNA+PAT+N/10/10:OPI+++SU:STEVENS+FO:CHARLES+TI:MR+MI:ANTHONY+FS:JOHN MARK'";

        var personName = PersonName.builder()
            .nhsNumber("N/10/10")
            .surname("STEVENS")
            .forename("CHARLES")
            .title("MR")
            .middleName("ANTHONY")
            .otherNames(new String[] { "JOHN", "MARK" })
            .build();

        assertEquals(expectedValue, personName.toEdifact());
    }

    @Test
    public void whenToEdifactWithOnlyMandatoryFields_thenReturnSegmentString() throws EdifactValidationException {
        var expectedValue = "PNA+PAT++++SU:STEVENS++++'";

        var personName = PersonName.builder()
            .surname("STEVENS")
            .build();

        assertEquals(expectedValue, personName.toEdifact());
    }
}
