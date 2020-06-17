package uk.nhs.digital.nhsconnect.nhais.parse;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

@Component
public class FhirParser {

    private final IParser parser;
//    private final FhirValidator validator;

    /*
     * TODO: Consider revisiting validation if we are able to update to FHIR 5.x
     * Current release (5.0.1) is broken: https://github.com/jamesagnew/hapi-fhir/issues/1852
     * 4.x validation seems to largely depend on a "schematron" mechanism which uses the library
     * "ph-schematron" which in turn seems to depend on maven plugins to work and is incompatible
     * with Gradle.
     */

    public FhirParser() {
        FhirContext ctx = FhirContext.forR4();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        parser = ctx.newJsonParser();
//        validator = ctx.newValidator();
//        IValidatorModule module1 = new SchemaBaseValidator(ctx);
//        IValidatorModule module2 = new SchematronBaseValidator(ctx);
//        validator.registerValidatorModule(module1);
//        validator.registerValidatorModule(module2);
    }

    public Patient parsePatient(String body) throws FhirValidationException {
        return parse(Patient.class, body);
    }

    public Patient getPatientFromParams(Parameters parameters) {
        return parameters.getParameter()
            .stream()
            .filter(param -> Patient.class.getSimpleName().equalsIgnoreCase(param.getName()))
            .map(Parameters.ParametersParameterComponent::getResource)
            .map(Patient.class::cast)
            .findFirst()
            .orElseThrow();
    }


    public Parameters parseParameters(String body) throws FhirValidationException {
        return parse(Parameters.class, body);
    }

//    private void validate(String resource) throws FhirValidationException {
//        ValidationResult result = validator.validateWithResult(resource);
//        if(!result.isSuccessful()) {
//            throw new FhirValidationException(result);
//        }
//    }

    private <T extends IBaseResource> T parse(Class<T> type, String body) throws FhirValidationException {
        try {
//            validate(body);
            return parser.parseResource(type, body);
        } catch (DataFormatException e) {
            throw new FhirValidationException("Unable to parse JSON resource as a " + type.getSimpleName() + ": " + e.getMessage());
        }
    }

    public IBaseResource parse(String body) {
        return parser.parseResource(body);
    }

    public String encodeToString(IBaseResource resource) {
        return parser.setPrettyPrint(true).encodeResourceToString(resource);
    }
}

