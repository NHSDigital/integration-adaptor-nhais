package uk.nhs.digital.nhsconnect.nhais.outbound;

import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.OperationOutcome;
import uk.nhs.digital.nhsconnect.nhais.rest.exception.BadRequestException;

public class FhirValidationException extends BadRequestException {

    public FhirValidationException(ValidationResult validationResult) {
        super(createMessage(validationResult));
    }

    public FhirValidationException(String message) {
        super(message);
    }

    public FhirValidationException(Throwable cause) {
        super(cause);
    }

    @Override
    public OperationOutcome.IssueType getIssueType() {
        return OperationOutcome.IssueType.STRUCTURE;
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

}
