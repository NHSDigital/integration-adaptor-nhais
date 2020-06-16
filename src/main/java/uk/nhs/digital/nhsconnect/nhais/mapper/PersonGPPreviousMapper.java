package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonGPPrevious;

import java.util.Objects;

public class PersonGPPreviousMapper implements FromFhirToEdifactMapper<PersonGPPrevious> {
    private final static String PREVIOUS_GP_PARAM = "previousGPName";

    public PersonGPPrevious map(Parameters parameters) {
        return PersonGPPrevious.builder()
            .practitioner(getPersonPreviousGP(parameters))
            .build();
    }

    private String getPersonPreviousGP(Parameters parameters) {
        return parameters.getParameter()
            .stream()
            .filter(param -> PREVIOUS_GP_PARAM.equalsIgnoreCase(param.getName()))
            .map(Parameters.ParametersParameterComponent::getValue)
            .map(Objects::toString)
            .map(this::splitPractitionerString)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Previous GP mapping problem"));
    }

    private String splitPractitionerString(String value) {
        return value.split("/")[1];
    }
}
