package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;

public class AcceptanceTypeMapper implements FromFhirToEdifactMapper<AcceptanceType> {
    private final static String ACCEPTANCE_TYPE = "acceptanceType";

    public AcceptanceType map(Parameters parameters) {
        return AcceptanceType.builder()
            .type(getAcceptanceType(parameters))
            .build();
    }

    private String getAcceptanceType(Parameters parameters) {
        return parameters.getParameter()
            .stream()
            .filter(param -> ACCEPTANCE_TYPE.equals(param.getName()))
            .map(Parameters.ParametersParameterComponent::getValue)
            .map(Object::toString)
            .map(AcceptanceType::getTypeValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Acceptance Type not supported"));
    }
}
