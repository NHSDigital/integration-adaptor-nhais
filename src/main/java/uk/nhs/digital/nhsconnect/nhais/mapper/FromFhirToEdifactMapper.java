package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

public interface FromFhirToEdifactMapper<T extends Segment> {
    T map(Parameters parameters);

    default Patient getPatient(Parameters parameters) {
        return parameters.getParameter()
                .stream()
                .filter(param -> Patient.class.getSimpleName().equalsIgnoreCase(param.getName()))
                .map(Parameters.ParametersParameterComponent::getResource)
                .map(Patient.class::cast)
                .findFirst()
                .orElseThrow();
    }
}
