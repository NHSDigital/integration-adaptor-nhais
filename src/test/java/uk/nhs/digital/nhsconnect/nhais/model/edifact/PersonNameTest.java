package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonNameTest {
    private final static String NHS_AND_NAMES = "PNA+PAT+RAT56:OBI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NHS_AND_NAMES_VALUE = "PAT+RAT56:OBI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NAMES_ONLY = "PNA+PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NAMES_ONLY_VALUE = "PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NHS_ONLY = "PNA+PAT+RAT56:OBI";
    private final static String NHS_ONLY_VALUE = "PAT+RAT56:OBI";


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
    public void When_BuildingNameWithTypeOnly_Then_ReturnCorrectValue() {
        var expectedValue = "PNA+PAT+T247:OPI'";

        var personName = PersonName.builder()
            .nhsNumber("T247")
            .patientIdentificationType("OPI")
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

    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        PersonName nhsAndNames = PersonName.builder()
            .nhsNumber("RAT56")
            .patientIdentificationType("OBI")
            .familyName("KENNEDY")
            .forename("SARAH")
            .title("MISS")
            .middleName("ANGELA")
            .build();

        String edifact = nhsAndNames.toEdifact();

        assertEquals("PNA+PAT+RAT56:OBI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'", edifact);
    }

    @Test
    void testFromString() {
        assertThat(PersonName.fromString(NHS_ONLY).getValue()).isEqualTo(NHS_ONLY_VALUE);
        assertThat(PersonName.fromString(NHS_AND_NAMES).getValue()).isEqualTo(NHS_AND_NAMES_VALUE);
        assertThat(PersonName.fromString(NAMES_ONLY).getValue()).isEqualTo(NAMES_ONLY_VALUE);
        assertThatThrownBy(() -> PersonName.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
