package uk.nhs.digital.nhsconnect.nhais.exceptions;

import ca.uhn.fhir.validation.ValidationResult;
import lombok.Getter;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.http.HttpStatus;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationOutcomeUtils;

public class FhirValidationException extends NhaisBaseException implements OperationOutcomeError {

    private IBaseOperationOutcome operationOutcome;

    public FhirValidationException(ValidationResult validationResult) {
        super(createMessage(validationResult));
        operationOutcome = validationResult.toOperationOutcome();
    }

    public FhirValidationException(String message) {
        super(message);
        operationOutcome = OperationOutcomeUtils.createFromMessage(message, OperationOutcome.IssueType.STRUCTURE);
    }

    @Override
    public IBaseOperationOutcome getOperationOutcome() {
        return this.operationOutcome;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
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
