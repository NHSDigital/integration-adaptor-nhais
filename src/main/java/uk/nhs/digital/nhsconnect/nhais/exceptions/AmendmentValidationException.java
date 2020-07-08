package uk.nhs.digital.nhsconnect.nhais.exceptions;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.http.HttpStatus;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationOutcomeUtils;

public class AmendmentValidationException extends NhaisBaseException implements OperationOutcomeError {

    private final IBaseOperationOutcome operationOutcome;

    public AmendmentValidationException(String message) {
        super(message);
        operationOutcome = OperationOutcomeUtils.createFromMessage(message, OperationOutcome.IssueType.INVALID);
    }

    @Override
    public IBaseOperationOutcome getOperationOutcome() {
        return operationOutcome;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
