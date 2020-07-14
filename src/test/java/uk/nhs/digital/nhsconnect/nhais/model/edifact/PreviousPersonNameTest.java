package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PreviousPersonNameTest {

    private final static String NHS_AND_NAMES = "PNA+PER+RAT56:OPI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NHS_AND_NAMES_VALUE = "PER+RAT56:OPI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NAMES_ONLY = "PNA+PER++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NAMES_ONLY_VALUE = "PER++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA";
    private final static String NHS_ONLY = "PNA+PER+RAT56:OPI";
    private final static String NHS_ONLY_VALUE = "PER+RAT56:OPI";


    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() throws EdifactValidationException {
        var expectedValue = "PNA+PER+1234567890:OPI+++SU:STEVENS+FO:CHARLES+TI:MR+MI:ANTHONY+FS:JOHN'";

        var personName = PreviousPersonName.builder()
            .nhsNumber("1234567890")
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
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
        var expectedValue = "PNA+PER+T247:OPI'";

        var personName = PreviousPersonName.builder()
            .nhsNumber("T247")
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .build();

        assertEquals(expectedValue, personName.toEdifact());
    }

    @Test
    public void When_BuildingEmptyName_Then_ReturnEmptySegment() {
        var expectedValue = "PNA+PER'";

        var personName = PreviousPersonName.builder()
            .build();

        assertEquals(expectedValue, personName.toEdifact());
    }

    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        PreviousPersonName nhsAndNames = PreviousPersonName.builder()
            .nhsNumber("RAT56")
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .familyName("KENNEDY")
            .forename("SARAH")
            .title("MISS")
            .middleName("ANGELA")
            .build();

        String edifact = nhsAndNames.toEdifact();

        assertEquals("PNA+PER+RAT56:OPI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'", edifact);
    }

    @Test
    void testFromString() {
        assertThat(PreviousPersonName.fromString(NHS_ONLY).getValue()).isEqualTo(NHS_ONLY_VALUE);
        assertThat(PreviousPersonName.fromString(NHS_AND_NAMES).getValue()).isEqualTo(NHS_AND_NAMES_VALUE);
        assertThat(PreviousPersonName.fromString(NAMES_ONLY).getValue()).isEqualTo(NAMES_ONLY_VALUE);
        assertThatThrownBy(() -> PreviousPersonName.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}