package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;

@RequiredArgsConstructor
public class ParametersExtension {

    private final Parameters parameters;

    public static Patient extractPatient(Parameters parameters) {
        return new ParametersExtension(parameters).extractPatient();
    }

    public Patient extractPatient() {
        return extractResource(ParameterNames.PATIENT, Patient.class);
    }

    public <T extends Resource> T extractResource(String name, Class<T> clazz) {
        return parameters.getParameter().stream()
            .filter(param -> name.equals(param.getName()))
            .map(Parameters.ParametersParameterComponent::getResource)
            .map(clazz::cast)
            .findFirst()
            .orElseThrow(() -> new FhirValidationException("Resource " + clazz.getSimpleName() + " with name " + name + " is missing in FHIR Parameters"));
    }

    public static String extractValue(Parameters parameters, String name) {
        return new ParametersExtension(parameters).extractValue(name);
    }

    public String extractValue(String name) {
        return Optional.ofNullable(parameters.getParameter(name))
            .map(StringType.class::cast)
            .map(StringType::getValueAsString)
            .orElseThrow(() -> new FhirValidationException("Value " + name + " is missing in FHIR Parameters"));
    }

    @SneakyThrows
    public String extractValueOrThrow(String name, Supplier<? extends Throwable> exception) {
        return Optional.ofNullable(parameters.getParameter(name))
            .map(StringType.class::cast)
            .map(StringType::getValueAsString)
            .orElseThrow(exception);
    }

    public int size() {
        return parameters.getParameter().size();
    }
}
