package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;
import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.PatientParameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonSexMapperTest {

    @Test
    void When_MappingGender_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        PersonSex personSex = new PersonSexMapper().map(parameters);

        var expectedPersonSex = PersonSex
            .builder()
            .gender(PersonSex.Gender.FEMALE)
            .build();

        assertEquals(expectedPersonSex, personSex);
    }

    @Test
    public void When_MappingGenderWrongType_Then_FhirValidationExceptionIsThrown() {
        Patient patient = new Patient();
        patient.setGender(Enumerations.AdministrativeGender.NULL);

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        assertThrows(FhirValidationException.class, () -> new PersonSexMapper().map(parameters));
    }

    @Test
    public void When_MappingWithoutGender_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        var personSexMapper = new PersonSexMapper();
        assertThrows(FhirValidationException.class, () -> personSexMapper.map(parameters));
    }
}
