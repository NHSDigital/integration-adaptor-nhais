package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfExit;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonDateOfExitMapperTest {

    private static final String DATE_STRING = "1991-11-06";
    private static final LocalDate LOCAL_DATE = LocalDate.parse(DATE_STRING);

    @Test
    void When_MappingDateOfEntry_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.EXIT_DATE)
            .setValue(new StringType(DATE_STRING));

        var personDateOfExitMapper = new PersonDateOfExitMapper();
        PersonDateOfExit personDateOfExit = personDateOfExitMapper.map(parameters);

        var expectedPersonDateOfEntry = new PersonDateOfExit(LOCAL_DATE);

        assertThat(personDateOfExit.toEdifact()).isEqualTo(expectedPersonDateOfEntry.toEdifact());
    }

    @Test
    public void When_MappingWithWrongDate_Then_DateTimeParseExceptionIsThrown() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.EXIT_DATE)
            .setValue(new StringType(""));

        var personDateOfExitMapper = new PersonDateOfExitMapper();
        assertThatThrownBy(() -> personDateOfExitMapper.map(parameters))
            .isExactlyInstanceOf(DateTimeParseException.class);
    }

    @Test
    public void When_MappingWithoutDateParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var PersonDateOfExitMapper = new PersonDateOfExitMapper();
        assertThatThrownBy(() -> PersonDateOfExitMapper.map(parameters))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

}