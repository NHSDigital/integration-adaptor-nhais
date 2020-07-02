package uk.nhs.digital.nhsconnect.nhais.exceptions;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.springframework.http.HttpStatus;

public class ParameterValidationException extends NhaisBaseException implements OperationOutcomeError {

    public ParameterValidationException(String message) {
        super(message);
    }

    @Override
    public IBaseOperationOutcome getOperationOutcome() {
        return null;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
