package uk.nhs.digital.nhsconnect.nhais.utils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;

import org.hl7.fhir.r4.model.Resource;

public class FhirToJson {

    public static void printResource(Resource resource){
        FhirContext ctx = FhirContext.forR4();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        IParser parser = ctx.newJsonParser();
        System.out.println(parser.setPrettyPrint(true).encodeResourceToString(resource));
    }

}
