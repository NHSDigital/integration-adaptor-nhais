package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionReasonCode;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeductionReasonCodeMapperTest {

    @Test
    void When_MappingAcceptanceCode_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.DEDUCTION_REASON_CODE)
            .setValue(new StringType("20"));

        var mapper = new DeductionReasonCodeMapper();
        DeductionReasonCode deductionReasonCode = mapper.map(parameters);

        var expectedDeductionReasonCode = DeductionReasonCode.builder()
            .code("20")
            .build();

        assertEquals(expectedDeductionReasonCode, deductionReasonCode);
    }

    @Test
    public void When_MappingWithoutCodeParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var mapper = new DeductionReasonCodeMapper();
        assertThrows(FhirValidationException.class, () -> mapper.map(parameters));
    }
}
