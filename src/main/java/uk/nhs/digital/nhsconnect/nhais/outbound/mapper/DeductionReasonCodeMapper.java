package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionReasonCode;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class DeductionReasonCodeMapper implements FromFhirToEdifactMapper<DeductionReasonCode> {

    public DeductionReasonCode map(Parameters parameters) {
        return DeductionReasonCode.builder()
            .code(getDeductionReasonCode(parameters))
            .build();
    }

    private String getDeductionReasonCode(Parameters parameters) {
        return ParametersExtension.extractOptionalValue(parameters, ParameterNames.DEDUCTION_REASON_CODE)
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new FhirValidationException("Parameter " + ParameterNames.DEDUCTION_REASON_CODE + " is required"));
    }
}
