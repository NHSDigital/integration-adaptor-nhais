package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDateOfBirthMapperTest {
    private static final Instant FIXED_TIME = ZonedDateTime.of(
        1991,
        11,
        6,
        23,
        55,
        0,
        0,
        ZoneId.of("Europe/London")).toInstant();


    @Test
    void When_MappingDob_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        patient.setBirthDate(Date.from(FIXED_TIME));

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personDobMapper = new PersonDateOfBirthMapper();
        PersonDateOfBirth personDateOfBirth = personDobMapper.map(parameters);

        var expectedPersonDob = PersonDateOfBirth
            .builder()
            .timestamp(FIXED_TIME)
            .build();

        assertEquals(expectedPersonDob, personDateOfBirth);

    }

    @Test
    public void When_MappingWithoutDob_Then_NullPointerExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personDobMapper = new PersonDateOfBirthMapper();
        assertThrows(NullPointerException.class, () -> personDobMapper.map(parameters));
    }
}
