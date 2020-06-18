package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptanceCodeMapperTest {

    @Test
    void When_MappingAcceptanceCode_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.ACCEPTANCE_CODE)
            .setValue(new StringType("S"));

        var acceptanceCodeMapper = new AcceptanceCodeMapper();
        AcceptanceCode acceptanceCode = acceptanceCodeMapper.map(parameters);

        var exceptedAcceptanceCode = AcceptanceCode.builder()
            .code("S")
            .build();

        assertEquals(exceptedAcceptanceCode, acceptanceCode);
    }

    @Test
    public void When_MappingWithoutCodeParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var acceptanceCodeMapper = new AcceptanceCodeMapper();
        assertThrows(FhirValidationException.class, () -> acceptanceCodeMapper.map(parameters));
    }
}
