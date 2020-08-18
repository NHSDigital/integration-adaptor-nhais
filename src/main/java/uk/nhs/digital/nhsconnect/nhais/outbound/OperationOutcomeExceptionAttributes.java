package uk.nhs.digital.nhsconnect.nhais.outbound;

import java.util.Map;

import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class OperationOutcomeExceptionAttributes extends DefaultErrorAttributes {

    @Autowired
    private FhirParser fhirParser;

    @Override
    public Map<String, Object> getErrorAttributes(
        WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes =
            super.getErrorAttributes(webRequest, includeStackTrace);
        errorAttributes.put("resorceType", "OperationOutcome");
        errorAttributes.remove("timestamp");
        errorAttributes.remove("path");
        errorAttributes.remove("message");

        return errorAttributes;
    }
}
