package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonNameMapperTest {

    @Test
    void When_MappingPatientName_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        HumanName humanName = new HumanName();
        humanName.setFamily("Smith");

        patient.setName(Collections.singletonList(humanName));
        patient.setIdentifier(List.of(new NhsIdentifier("1234567890")));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personNameMapper = new PersonNameMapper();
        PersonName personName = personNameMapper.map(parameters);

        var expectedPersonName = PersonName
            .builder()
            .nhsNumber("1234567890")
            .familyName("Smith")
            .patientIdentificationType(PersonName.PatientIdentificationType.OPI)
            .build();

        assertEquals(expectedPersonName.toEdifact(), personName.toEdifact());
    }

    @Test
    public void When_MappingWithoutNhs_Then_IllegalStateExceptionIsThrown() {
        Patient patient = new Patient();
        HumanName humanName = new HumanName();
        humanName.setFamily("Smith");

        patient.setName(Collections.singletonList(humanName));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personNameMapper = new PersonNameMapper();
        assertThrows(IllegalStateException.class, () -> personNameMapper.map(parameters));
    }

    @Test
    public void When_MappingWithoutSurname_Then_UnsupportedOperationExceptionIsThrown() {
        Patient patient = new Patient();
        patient.setIdentifier(List.of(new NhsIdentifier("1234567890")));
        patient.setName(List.of());

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personNameMapper = new PersonNameMapper();
        assertThrows(UnsupportedOperationException.class, () -> personNameMapper.map(parameters));
    }

    @Test
    public void When_MappingWithoutPatient_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personNameMapper = new PersonNameMapper();
        assertThrows(FhirValidationException.class, () -> personNameMapper.map(parameters));
    }
}
