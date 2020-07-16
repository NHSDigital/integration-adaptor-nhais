package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDateOfBirthMapperTest {
    private static final Instant FIXED_TIME =
        LocalDate.of(1991, 11, 6)
            .atStartOfDay(ZoneId.of("Europe/London"))
            .toInstant();
    private static final Instant FIXED_TIME_LOCAL = LocalDate.of(1991, 11, 6)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant();
    private final PersonDateOfBirthMapper personDateOfBirthMapper = new PersonDateOfBirthMapper();

    @Test
    void When_MappingDob_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        patient.setBirthDate(Date.from(FIXED_TIME_LOCAL));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        PersonDateOfBirth personDateOfBirth = personDateOfBirthMapper.map(parameters);

        var expectedPersonDob = PersonDateOfBirth
            .builder()
            .timestamp(FIXED_TIME)
            .build();

        assertEquals(expectedPersonDob, personDateOfBirth);
    }

    @Test
    public void When_MappingWithoutDob_Then_NullPointerExceptionIsThrown() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        assertThrows(NullPointerException.class, () -> personDateOfBirthMapper.map(parameters));
    }

    @Test
    public void When_ParametersWithoutDob_Then_CanNotMap() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        assertThat(personDateOfBirthMapper.inputDataExists(parameters)).isFalse();
    }

    @Test
    public void When_ParametersWithDob_Then_CanMap() {
        Patient patient = new Patient();
        patient.setBirthDate(Date.from(FIXED_TIME_LOCAL));
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        assertThat(personDateOfBirthMapper.inputDataExists(parameters)).isTrue();
    }
}
