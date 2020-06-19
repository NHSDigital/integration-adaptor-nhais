package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

public interface FromFhirToEdifactMapper<T extends Segment> {
    static <T extends Segment> FromFhirToEdifactMapper<T> emptyMapper(T segment) {
        return parameters -> segment;
    }

    T map(Parameters parameters);
}