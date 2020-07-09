package uk.nhs.digital.nhsconnect.nhais.exceptions;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.http.HttpStatus;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationOutcomeUtils;

public class PatchValidationException extends NhaisBaseException implements OperationOutcomeError {

    private final IBaseOperationOutcome operationOutcome;

    public PatchValidationException(String message) {
        super(message);
        operationOutcome = OperationOutcomeUtils.createFromMessage(message, OperationOutcome.IssueType.STRUCTURE);
    }

    public PatchValidationException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.operationOutcome = OperationOutcomeUtils.createFromMessage(cause.getMessage(), OperationOutcome.IssueType.STRUCTURE);
    }

    @Override
    public IBaseOperationOutcome getOperationOutcome() {
        return this.operationOutcome;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
