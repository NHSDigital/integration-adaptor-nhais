package uk.nhs.digital.nhsconnect.nhais.exceptions;

import ca.uhn.fhir.validation.ValidationResult;
import lombok.Getter;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;

@Getter
public class FhirValidationException extends NhaisBaseException {

    private IBaseOperationOutcome operationOutcome;

    public FhirValidationException(ValidationResult validationResult) {
        super(createMessage(validationResult));
        operationOutcome = validationResult.toOperationOutcome();
    }

    public FhirValidationException(String message) {
        super(message);
        operationOutcome = null;
    }

    private static String createMessage(ValidationResult validationResult) {
        int numberOfMessages = validationResult.getMessages().size();
        StringBuilder b = new StringBuilder("JSON FHIR Resource failed validation");
        if(numberOfMessages >= 1) {
            b.append(": ").append(validationResult.getMessages().get(0).getMessage());
        }
        if(numberOfMessages >= 2) {
            b.append(" (and ").append(numberOfMessages - 1).append(" more error messages truncated)");
        }
        return b.toString();
    }

    public FhirValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
