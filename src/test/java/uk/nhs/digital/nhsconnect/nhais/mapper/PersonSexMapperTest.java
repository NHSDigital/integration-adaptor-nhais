package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonSexMapperTest {

    @Test
    void When_MappingGender_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);

        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName(Patient.class.getSimpleName())
                .setResource(patient);

        var personSexMapper = new PersonSexMapper();
        PersonSex personSex = personSexMapper.map(parameters);

        var expectedPersonSex = PersonSex
                .builder()
                .sexCode("Female")
                .build();

        assertEquals(expectedPersonSex, personSex);

    }

    @Test
    public void When_MappingGenderWrongType_Then_NoSuchElementExceptionIsThrown() {
        Patient patient = new Patient();
        patient.setGender(Enumerations.AdministrativeGender.NULL);

        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName(Patient.class.getSimpleName())
                .setResource(patient);

        var acceptanceTypeMapper = new AcceptanceTypeMapper();
        assertThrows(NoSuchElementException.class, () -> acceptanceTypeMapper.map(parameters));
    }
}
