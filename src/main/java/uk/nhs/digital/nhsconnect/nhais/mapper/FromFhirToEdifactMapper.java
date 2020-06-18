package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

@Component
public interface FromFhirToEdifactMapper<T extends Segment> {
    T map(Parameters parameters);
}