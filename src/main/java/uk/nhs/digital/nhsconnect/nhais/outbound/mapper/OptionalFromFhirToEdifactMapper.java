package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

public interface OptionalFromFhirToEdifactMapper<T extends Segment> extends FromFhirToEdifactMapper<T>{

    boolean inputDataExists(Parameters parameters);

}