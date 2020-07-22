package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentificationType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PatientName;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.PatientParameter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonNameMapperTest {

    public static final String NHS_NUMBER = "1234567890";
    public static final String FAMILY_NAME = "Smith";

    @Test
    void When_MappingPatientFamilyName_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        PatientName patientName = PatientName.builder()
            .familyName(FAMILY_NAME)
            .build();

        patient.setName(List.of(patientName));
        patient.setIdentifier(List.of(new NhsIdentifier(NHS_NUMBER)));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personNameMapper = new PersonNameMapper();
        PersonName personName = personNameMapper.map(parameters);

        var expectedPersonName = PersonName
            .builder()
            .nhsNumber(NHS_NUMBER)
            .surname(FAMILY_NAME)
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .build();

        assertEquals(expectedPersonName.toEdifact(), personName.toEdifact());
    }

    @Test
    void When_MappingAllPossiblePatientNames_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        PatientName patientName = PatientName.builder()
            .familyName(FAMILY_NAME)
            .forename("Forename")
            .middleName("middleName")
            .thirdForename("third Forename")
            .title("title")
            .build();

        patient.setName(List.of(patientName));
        patient.setIdentifier(List.of(new NhsIdentifier(NHS_NUMBER)));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personNameMapper = new PersonNameMapper();
        PersonName personName = personNameMapper.map(parameters);

        var expectedPersonName = PersonName
            .builder()
            .nhsNumber(NHS_NUMBER)
            .surname(FAMILY_NAME)
            .firstForename("Forename")
            .secondForename("middleName")
            .otherForenames("third Forename")
            .title("title")
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .build();

        assertEquals(expectedPersonName.toEdifact(), personName.toEdifact());
    }

    @Test
    public void When_MappingWithoutSurname_Then_UnsupportedOperationExceptionIsThrown() {
        Patient patient = new Patient();
        patient.setIdentifier(List.of(new NhsIdentifier(NHS_NUMBER)));
        patient.setName(List.of());

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personNameMapper = new PersonNameMapper();
        assertThrows(UnsupportedOperationException.class, () -> personNameMapper.map(parameters));
    }

    @Test
    void When_MappingWithoutNhsNumber_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        HumanName humanName = new HumanName();
        humanName.setFamily(FAMILY_NAME);

        patient.setName(List.of(humanName));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personNameMapper = new PersonNameMapper();
        PersonName personName = personNameMapper.map(parameters);

        var expectedPersonName = PersonName.builder()
            .surname(FAMILY_NAME)
            .build();

        assertEquals(expectedPersonName.toEdifact(), personName.toEdifact());
    }

    @Test
    public void When_MappingWithoutPatient_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personNameMapper = new PersonNameMapper();
        assertThrows(FhirValidationException.class, () -> personNameMapper.map(parameters));
    }
}
