package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

public interface FromFhirToEdifactMapper<T extends Segment> {
    T map(Parameters parameters);

    default Patient getPatient(Parameters parameters) {
        FhirParser fhirParser = new FhirParser();
        return fhirParser.getPatientFromParams(parameters);
    }
}
