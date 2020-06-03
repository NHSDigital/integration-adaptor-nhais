package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonNameTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() throws EdifactValidationException {
        var expectedValue = "PNA+PAT+N/10/10:OPI+++SU:STEVENS+FO:CHARLES+TI:MR+MI:ANTHONY+FS:JOHN MARK'";

        var personName = PersonName.builder()
                .nhsNumber("N/10/10")
                .surname("STEVENS")
                .forename("CHARLES")
                .title("MR")
                .middleName("ANTHONY")
                .otherNames(new String[]{"JOHN", "MARK"})
                .build();

        assertEquals(expectedValue, personName.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithMandatoryFields_Then_ReturnCorrectString() throws EdifactValidationException {
        var expectedValue = "PNA+PAT++++SU:STEVENS++++'";

        var personName = PersonName.builder()
                .surname("STEVENS")
                .build();

        assertEquals(expectedValue, personName.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptySurname_Then_EdifactValidationExceptionIsThrown() {
        var personName = PersonName.builder()
                .surname(StringUtils.EMPTY)
                .build();

        assertThrows(EdifactValidationException.class, personName::toEdifact);
    }

    @Test
    public void When_BuildingWithoutSurname_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonName.builder().build());
    }
}
