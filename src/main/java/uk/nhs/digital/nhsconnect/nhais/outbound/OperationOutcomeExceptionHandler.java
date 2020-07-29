package uk.nhs.digital.nhsconnect.nhais.outbound;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.nhs.digital.nhsconnect.nhais.rest.exception.OperationOutcomeError;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationOutcomeUtils;

import static java.util.Collections.singletonList;

@ControllerAdvice
@RestController
@Slf4j
public class OperationOutcomeExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private FhirParser fhirParser;

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllErrors(Exception ex, WebRequest request) {
        LOGGER.error("Creating OperationOutcome response for unhandled exception", ex);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("content-type", singletonList("application/json"));
        if (ex instanceof OperationOutcomeError) {
            OperationOutcomeError error = (OperationOutcomeError) ex;
            String content = fhirParser.encodeToString(error.getOperationOutcome());
            return new ResponseEntity<>(content, headers, error.getStatusCode());
        }
        OperationOutcome operationOutcome = OperationOutcomeUtils.createFromMessage(ex.getMessage());
        String content = fhirParser.encodeToString(operationOutcome);
        return new ResponseEntity<>(content, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
