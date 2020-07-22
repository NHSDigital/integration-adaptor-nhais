package uk.nhs.digital.nhsconnect.nhais.rest.exception;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.springframework.http.HttpStatus;

public interface OperationOutcomeError {
    IBaseOperationOutcome getOperationOutcome();
    HttpStatus getStatusCode();
}
