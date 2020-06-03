package uk.nhs.digital.nhsconnect.nhais.mapper;

import com.google.common.collect.ImmutableSet;
import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;

import java.util.NoSuchElementException;
import java.util.Set;

public class AcceptanceCodeMapper implements FromFhirToEdifactMapper<AcceptanceCode> {
    private final static String ACCEPTANCE_CODE = "acceptanceCode";
    private final static Set<String> ALLOWED_CODES = ImmutableSet.of("A", "D", "R", "I", "S");

    public AcceptanceCode map(Parameters parameters) {
        return AcceptanceCode.builder()
                .code(getAcceptanceCode(parameters))
                .build();
    }

    private String getAcceptanceCode(Parameters parameters) {
        String inputValue = parameters.getParameter()
                .stream()
                .filter(param -> ACCEPTANCE_CODE.equals(param.getName()))
                .map(Parameters.ParametersParameterComponent::getValue)
                .map(Object::toString)
                .findFirst()
                .orElseThrow();

        if (ALLOWED_CODES.contains(inputValue)) {
            return inputValue;
        } else {
            throw new NoSuchElementException("Acceptance Code not supported: " + inputValue);
        }
    }
}
