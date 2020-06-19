package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class AcceptanceCodeMapper implements FromFhirToEdifactMapper<AcceptanceCode> {

    public AcceptanceCode map(Parameters parameters) {
        return AcceptanceCode.builder()
            .code(getAcceptanceCode(parameters))
            .build();
    }

    private String getAcceptanceCode(Parameters parameters) {
        ParametersExtension parametersExt = new ParametersExtension(parameters);
        return parametersExt.extractValue(ParameterNames.ACCEPTANCE_CODE);
    }
}
