package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;

public class AcceptanceCodeMapper implements FromFhirToEdifactMapper<AcceptanceCode> {
    private final static String ACCEPTANCE_CODE = "acceptanceCode";

    public AcceptanceCode map(Parameters parameters) {
        return AcceptanceCode.builder()
            .code(getAcceptanceCode(parameters))
            .build();
    }

    private String getAcceptanceCode(Parameters parameters) {
        return parameters.getParameter()
            .stream()
            .filter(param -> ACCEPTANCE_CODE.equals(param.getName()))
            .map(Parameters.ParametersParameterComponent::getValue)
            .map(Object::toString)
            .findFirst()
            .orElseThrow(() -> new FhirValidationException("Error while parsing param: " + ACCEPTANCE_CODE));
    }
}
