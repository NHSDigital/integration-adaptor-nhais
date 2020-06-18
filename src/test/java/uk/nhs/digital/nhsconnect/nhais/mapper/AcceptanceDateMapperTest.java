package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptanceDateMapperTest {

    @Test
    void When_MappingAcceptanceCode_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.ACCEPTANCE_DATE)
            .setValue(new StringType("2020-06-05"));

        var acceptanceDateMapper = new AcceptanceDateMapper();
        DateTimePeriod dateTimePeriod = acceptanceDateMapper.map(parameters);

        var expectedInstant = LocalDate.parse("2020-06-05").atStartOfDay(ZoneOffset.UTC).toInstant();
        var expectedDateTimePeriod = new DateTimePeriod(expectedInstant, DateTimePeriod.TypeAndFormat.ACCEPTANCE_DATE);

        assertEquals(expectedDateTimePeriod, dateTimePeriod);
    }

}
