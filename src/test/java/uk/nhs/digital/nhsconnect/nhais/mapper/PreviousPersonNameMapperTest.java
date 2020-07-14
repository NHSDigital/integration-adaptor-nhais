package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentificationType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PreviousPersonName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PatientName;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreviousPersonNameMapperTest {

    public static final String NHS_NUMBER = "1234567890";
    public static final String FAMILY_NAME = "Smith";
    public static final String FAMILY_NAME_2 = "Kowalski";

    public static final PatientName patientName = PatientName.builder()
        .familyName(FAMILY_NAME)
        .build();

    public static final PatientName patientName2 = PatientName.builder()
        .familyName(FAMILY_NAME_2)
        .build();

    @Test
    void When_MappingPatientPreviousFamilyName_Then_ExpectCorrectResult() {
        Patient patient = new Patient()
            .setName(List.of(patientName, patientName2))
            .setIdentifier(List.of(new NhsIdentifier(NHS_NUMBER)));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var previousPersonNameMapper = new PreviousPersonNameMapper();
        PreviousPersonName personName = previousPersonNameMapper.map(parameters);

        var expectedPersonName = PreviousPersonName
            .builder()
            .nhsNumber(NHS_NUMBER)
            .familyName(FAMILY_NAME_2)
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .build();

        assertThat(expectedPersonName.toEdifact()).isEqualTo(personName.toEdifact());
    }

    @Test
    void When_MappingAllPossiblePatientNames_Then_ExpectCorrectResult() {
        PatientName patientName = PatientName.builder()
            .familyName(FAMILY_NAME)
            .forename("Forename")
            .middleName("middleName")
            .thirdForename("third Forename")
            .title("title")
            .build();

        PatientName patientName2 = PatientName.builder()
            .familyName(FAMILY_NAME_2)
            .forename("Forename")
            .middleName("middleName")
            .thirdForename("third Forename")
            .title("title")
            .build();

        Patient patient = new Patient()
            .setName(List.of(patientName, patientName2))
            .setIdentifier(List.of(new NhsIdentifier(NHS_NUMBER)));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var previousPersonNameMapper = new PreviousPersonNameMapper();
        PreviousPersonName personName = previousPersonNameMapper.map(parameters);

        var expectedPersonName = PreviousPersonName
            .builder()
            .nhsNumber(NHS_NUMBER)
            .familyName(FAMILY_NAME_2)
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .build();

        assertThat(expectedPersonName.toEdifact()).isEqualTo(personName.toEdifact());
    }

    @Test
    public void When_MappingWithoutSurname_Then_FhirValidationExceptionIsThrown() {
        Patient patient = new Patient();
        patient.setIdentifier(List.of(new NhsIdentifier(NHS_NUMBER)));
        patient.setName(List.of());

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var previousPersonNameMapper = new PreviousPersonNameMapper();
        assertThrows(FhirValidationException.class, () -> previousPersonNameMapper.map(parameters));
    }

    @Test
    void When_MappingWithoutNhsNumber_Then_ExpectCorrectResult() {
        Patient patient = new Patient()
            .setName(List.of(patientName, patientName2));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var previousPersonNameMapper = new PreviousPersonNameMapper();
        PreviousPersonName personName = previousPersonNameMapper.map(parameters);

        var expectedPersonName = PreviousPersonName.builder()
            .familyName(FAMILY_NAME_2)
            .build();

        assertThat(expectedPersonName.toEdifact()).isEqualTo(personName.toEdifact());
    }

    @Test
    public void When_MappingWithoutPatient_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var previousPersonNameMapper = new PreviousPersonNameMapper();
        assertThrows(FhirValidationException.class, () -> previousPersonNameMapper.map(parameters));
    }

    @Test
    void When_ThereIsOnlyOneName_Then_CantMap() {
        Patient patient = new Patient()
            .setName(List.of(patientName));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var previousPersonNameMapper = new PreviousPersonNameMapper();

        assertThat(previousPersonNameMapper.canMap(parameters)).isFalse();
    }

    @Test
    void When_ThereAreTwoNamea_Then_CanMap() {
        Patient patient = new Patient()
            .setName(List.of(patientName, patientName2));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var previousPersonNameMapper = new PreviousPersonNameMapper();

        assertThat(previousPersonNameMapper.canMap(parameters)).isTrue();
    }
}