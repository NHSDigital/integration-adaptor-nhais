package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceDate;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcceptanceDateMapperTest {

    @Test
    void When_MappingAcceptanceCode_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.ACCEPTANCE_DATE)
            .setValue(new StringType("2020-06-05"));

        var acceptanceDateMapper = new AcceptanceDateMapper();
        var acceptanceDate = acceptanceDateMapper.map(parameters);

        var expectedDate = LocalDate.parse("2020-06-05");
        var expectedAcceptanceDate = new AcceptanceDate(expectedDate);

        assertEquals(expectedAcceptanceDate, acceptanceDate);
    }

}
