package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.PatientParameter;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDateOfBirthMapperTest {
    private static final LocalDate FIXED_TIME = LocalDate.parse("1991-11-06");
    private static final Instant FIXED_TIME_LOCAL = LocalDate.parse("1991-11-06")
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant();
    private final PersonDateOfBirthMapper personDateOfBirthMapper = new PersonDateOfBirthMapper();

    @Test
    void When_MappingDob_Expect_ExpectCorrectResult() {
        Patient patient = new Patient();
        patient.setBirthDate(Date.from(FIXED_TIME_LOCAL));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        PersonDateOfBirth personDateOfBirth = personDateOfBirthMapper.map(parameters);

        var expectedPersonDob = PersonDateOfBirth
            .builder()
            .dateOfBirth(FIXED_TIME)
            .build();

        assertEquals(expectedPersonDob, personDateOfBirth);
    }

    @Test
    public void When_MappingWithoutDob_Expect_NullPointerExceptionIsThrown() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        assertThrows(NullPointerException.class, () -> personDateOfBirthMapper.map(parameters));
    }

    @Test
    public void When_ParametersWithoutDob_Expect_CanNotMap() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        assertThat(personDateOfBirthMapper.inputDataExists(parameters)).isFalse();
    }

    @Test
    public void When_ParametersWithDob_Expect_CanMap() {
        Patient patient = new Patient();
        patient.setBirthDate(Date.from(FIXED_TIME_LOCAL));
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        assertThat(personDateOfBirthMapper.inputDataExists(parameters)).isTrue();
    }
}
