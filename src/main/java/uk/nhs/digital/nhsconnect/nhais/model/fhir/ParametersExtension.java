package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ParametersExtension {

    private final Parameters parameters;

    public static Patient extractPatient(Parameters parameters) {
        return new ParametersExtension(parameters).extractPatient();
    }

    public static Optional<String> extractExtensionValue(Parameters parameters, String extensionUrl) {
        return extractPatient(parameters)
            .getExtension()
            .stream()
            .filter(extension -> extensionUrl.equalsIgnoreCase(extension.getUrl()))
            .map(Extension::getValue)
            .filter(Objects::nonNull)
            .map(Type::primitiveValue)
            .filter(Objects::nonNull)
            .findFirst();
    }

    public Patient extractPatient() {
        return extractResource(ParameterNames.PATIENT, Patient.class);
    }

    public static AcceptanceType.AvailableTypes extractAcceptanceType(Parameters parameters) {
        return new ParametersExtension(parameters).extractAcceptanceType();
    }

    public AcceptanceType.AvailableTypes extractAcceptanceType() {
        String acceptanceTypeCode = extractValue(ParameterNames.ACCEPTANCE_TYPE);
        return AcceptanceType.AvailableTypes.fromCode(acceptanceTypeCode);
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
        return extractOptionalValue(name)
            .orElseThrow(() -> new FhirValidationException("Value " + name + " is missing in FHIR Parameters"));
    }

    public static Optional<String> extractOptionalValue(Parameters parameters, String name) {
        return new ParametersExtension(parameters).extractOptionalValue(name);
    }

    public Optional<String> extractOptionalValue(String name) {
        return Optional.ofNullable(parameters.getParameter(name))
            .map(StringType.class::cast)
            .map(StringType::getValueAsString);
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
