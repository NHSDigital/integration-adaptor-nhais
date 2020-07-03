package uk.nhs.digital.nhsconnect.nhais.exceptions;

import uk.nhs.digital.nhsconnect.nhais.utils.OperationOutcomeUtils;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.http.HttpStatus;

public class ParameterValidationException extends NhaisBaseException implements OperationOutcomeError {

    private final IBaseOperationOutcome operationOutcome;

    public ParameterValidationException(String message) {
        super(message);
        operationOutcome = OperationOutcomeUtils.createFromMessage(message, OperationOutcome.IssueType.NOTFOUND);
    }

    @Override
    public IBaseOperationOutcome getOperationOutcome() {
        return operationOutcome;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
