package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;
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
        ParametersExtension parametersExt = new ParametersExtension(parameters);
        return parametersExt.extractValue(ParameterNames.DEDUCTION_REASON_CODE);
    }
}
