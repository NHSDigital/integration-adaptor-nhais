package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPlaceOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.PatientParameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonPlaceOfBirthMapperTest {

    private final PersonPlaceOfBirthMapper personPlaceOfBirthMapper = new PersonPlaceOfBirthMapper();

    @Test
    void When_ExtensionExistsAndValueIsSet_Expect_CanMap() {
        Patient patient = new Patient();
        patient.addExtension(new BirthPlaceExtension("GLASGOW"));
        PatientParameter patientParameter = new PatientParameter(patient);
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        assertThat(personPlaceOfBirthMapper.inputDataExists(parameters)).isTrue();
    }

    @Test
    void When_ExtensionExistsAndValueIsNotSet_Expect_CanMap() {
        Patient patient = new Patient();
        patient.addExtension(new BirthPlaceExtension(""));
        PatientParameter patientParameter = new PatientParameter(patient);
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        assertThat(personPlaceOfBirthMapper.inputDataExists(parameters)).isTrue();
        PersonPlaceOfBirth personPlaceOfBirth = personPlaceOfBirthMapper.map(parameters);
        assertThatThrownBy(() -> personPlaceOfBirth.preValidate())
            .isExactlyInstanceOf(EdifactValidationException.class);
    }

    @Test
    void When_ExtensionDoesntExist_Expect_CanNotMap() {
        PatientParameter patientParameter = new PatientParameter();
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        assertThat(personPlaceOfBirthMapper.inputDataExists(parameters)).isFalse();
    }

    @Test
    void When_ExtensionExistsAndValueIsSet_Expect_MappingSuccessful() {
        Patient patient = new Patient();
        patient.addExtension(new BirthPlaceExtension("GLASGOW"));
        PatientParameter patientParameter = new PatientParameter(patient);
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        PersonPlaceOfBirth personPlaceOfBirth = personPlaceOfBirthMapper.map(parameters);

        PersonPlaceOfBirth expected = new PersonPlaceOfBirth("GLASGOW");

        assertThat(personPlaceOfBirth.toEdifact()).isEqualTo(expected.toEdifact());
        assertThat(personPlaceOfBirth.toEdifact()).isEqualTo("LOC+950+GLASGOW'");
    }
}