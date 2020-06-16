package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import org.junit.jupiter.api.Test;

class PatientIdentifierTest {

    private final static String NHS_AND_NAMES = "PNA+PAT+RAT56:OBI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NHS_AND_NAMES_VALUE = "PAT+RAT56:OBI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NAMES_ONLY = "PNA+PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NAMES_ONLY_VALUE = "PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NHS_ONLY = "PNA+PAT+RAT56:OBI";
    private final static String NHS_ONLY_VALUE = "PAT+RAT56:OBI";

    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        PatientIdentifier nhsAndNames = PatientIdentifier.builder()
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
        PatientIdentifier onlyNhs = PatientIdentifier.builder()
            .nhsNumber("RAT56")
            .patientIdentificationType("OBI")
            .build();

        PatientIdentifier nhsAndNames = PatientIdentifier.builder()
            .nhsNumber("RAT56")
            .patientIdentificationType("OBI")
            .familyName("KENNEDY")
            .forename("SARAH")
            .title("MISS")
            .middleName("ANGELA")
            .build();

        assertThat(PatientIdentifier.fromString(NHS_ONLY).getValue()).isEqualTo(NHS_ONLY_VALUE);
        assertThat(PatientIdentifier.fromString(NHS_AND_NAMES).getValue()).isEqualTo(NHS_AND_NAMES_VALUE);
        assertThat(PatientIdentifier.fromString(NAMES_ONLY).getValue()).isEqualTo(NAMES_ONLY_VALUE);
        assertThatThrownBy(() -> PatientIdentifier.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}