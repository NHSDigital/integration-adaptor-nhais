package uk.nhs.digital.nhsconnect.nhais.parse;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class FhirParser {
    private final FhirContext ctx = FhirContext.forR4();
    private final IParser parser = ctx.newJsonParser();

    public Patient parse(String body) {
        //TODO just an example:
        // LOGGER.info("FhirParser, parsing: " + body);
        return parser.parseResource(Patient.class, body);
    }

    public String encodeToString(Patient patient) {
        return parser.encodeResourceToString(patient);
    }

    public String encodeToString(Parameters parameters) {
        return parser.encodeResourceToString(parameters);
    }
}

