package uk.nhs.digital.nhsconnect.nhais.utils;

import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

public class MissingOrEmptyElementUtils {

    public static void exceptionIfMissingOrEmpty(String path, Object value) throws FhirValidationException {
        if (value == null) {
            throw new FhirValidationException("Missing element at " + path);
        }
        if (value instanceof List) {
            List list = (List) value;
            if (list.isEmpty()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        } else if (value instanceof String) {
            String str = (String) value;
            if (str.isBlank()) {
                throw new FhirValidationException("Missing element at " + path);
            }
        }
    }
}
