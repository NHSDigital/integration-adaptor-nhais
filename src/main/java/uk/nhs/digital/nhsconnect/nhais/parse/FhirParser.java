package uk.nhs.digital.nhsconnect.nhais.parse;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

@Component
public class FhirParser {

    private final FhirContext ctx;
    private final IParser parser;
    private final FhirValidator validator;

    public FhirParser() {
        ctx = FhirContext.forR4();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        parser = ctx.newJsonParser();
        validator = ctx.newValidator();
        validator.setValidateAgainstStandardSchema(true);
    }

    public Patient parsePatient(String body) throws FhirValidationException {
        return parse(Patient.class, body);
    }

    private void validate(String resource) throws FhirValidationException {
        ValidationResult result = validator.validateWithResult(resource);
        if(!result.isSuccessful()) {
            throw new FhirValidationException(result);
        }
    }

    private <T extends IBaseResource> T parse(Class<T> type, String body) throws FhirValidationException {
        try {
            validate(body);
            return parser.parseResource(type, body);
        } catch (DataFormatException e) {
            throw new FhirValidationException("Unable to parse JSON resource as a " + type.getSimpleName() + ": " + e.getMessage());
        }
    }

    public String encodeToString(Patient patient) {
        return parser.encodeResourceToString(patient);
    }

    public String encodeToString(Parameters parameters) {
        return parser.encodeResourceToString(parameters);
    }
}

