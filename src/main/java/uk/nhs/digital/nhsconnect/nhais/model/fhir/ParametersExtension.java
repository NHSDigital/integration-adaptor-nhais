package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.ParameterNames;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;

@RequiredArgsConstructor
public class ParametersExtension {

    private final Parameters parameters;

    public Patient extractPatient() {
        return extractResource(ParameterNames.PATIENT, Patient.class);
    }

    public <T extends Resource> T extractResource(String name, Class<T> clazz) {
        return parameters.getParameter().stream()
            .filter(param -> name.equals(param.getName()))
            .map(Parameters.ParametersParameterComponent::getResource)
            .map(clazz::cast)
            .findFirst()
            .orElseThrow();
    }

    public String extractValue(String name) {
        return Optional.ofNullable(parameters.getParameter(name))
            .map(StringType.class::cast)
            .map(StringType::getValueAsString)
            .orElseThrow();
    }
}
