package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcceptanceTypeMapperTest {

    @Test
    void When_MappingAcceptanceType_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName("acceptanceType")
                .setValue(new StringType("1"));

        var acceptanceTypeMapper = new AcceptanceTypeMapper();
        AcceptanceType acceptanceType = acceptanceTypeMapper.map(parameters);

        var exceptedAcceptanceType = AcceptanceType.builder()
                .type("1")
                .build();

        assertEquals(exceptedAcceptanceType, acceptanceType);
    }
}
