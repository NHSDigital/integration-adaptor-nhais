package uk.nhs.digital.nhsconnect.nhais.mapper;

import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AcceptanceTypeMapper implements FromFhirToEdifactMapper<AcceptanceType> {
    private final static String ACCEPTANCE_TYPE = "acceptanceType";
    private final static Map<String, String> ACC_TYPE_MAPPING = ImmutableMap.of(
            "birth", "1",
            "first", "2",
            "transferin", "3",
            "immigrant", "4",
            "exservices", "5"
    );

    public AcceptanceType map(Parameters parameters) {
        return AcceptanceType.builder()
                .type(getAcceptanceType(parameters))
                .build();
    }

    private String getAcceptanceType(Parameters parameters) {
        String inputValue = parameters.getParameter()
                .stream()
                .filter(param -> ACCEPTANCE_TYPE.equals(param.getName()))
                .map(Parameters.ParametersParameterComponent::getValue)
                .map(Object::toString)
                .findFirst()
                .orElseThrow();

        return Optional.ofNullable(ACC_TYPE_MAPPING.get(inputValue))
                .orElseThrow(() -> new NoSuchElementException("acceptanceType element not found"));
    }
}
