package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionDate;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeductionDateMapperTest {

    @Test
    void When_MappingDeductionDate_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.DATE_OF_DEDUCTION)
            .setValue(new StringType("2020-06-05"));

        var deductionDateMapper = new DeductionDateMapper();
        DeductionDate deductionDate = deductionDateMapper.map(parameters);

        var expectedDeductionDate = new DeductionDate(LocalDate.parse("2020-06-05"));

        assertEquals(expectedDeductionDate, deductionDate);
    }

}
