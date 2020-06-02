package uk.nhs.digital.nhsconnect.nhais.mapper;

import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;

import java.util.Map;

public class AcceptanceTypeMapper implements FromFhirToEdifactMapper<AcceptanceType> {
    private final static String ACCEPTANCE_TYPE = "acceptanceType";
    private final static Map ACC_TYPE_MAPPING = ImmutableMap.of(
            "1", "Birth",
            "2", "1st Acceptance",
            "3", "Transfer-in",
            "4", "Immigrant",
            "5", "Ex-services"
    );

    public AcceptanceType map(Parameters parameters) {
        return AcceptanceType.builder()
                .type(getAcceptanceType(parameters))
                .build();
    }

    private String getAcceptanceType(Parameters parameters) {
        System.out.println(parameters.getParameterFirstRep().getValue());

        return parameters.getParameter()
                .stream()
                .filter(param -> ACCEPTANCE_TYPE.equals(param.getName()))
                .map(Parameters.ParametersParameterComponent::getValue)
                .map(Object::toString)
                .findFirst()
                .orElseThrow();
    }
}
