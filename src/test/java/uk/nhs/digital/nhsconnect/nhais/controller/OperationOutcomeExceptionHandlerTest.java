package uk.nhs.digital.nhsconnect.nhais.controller;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.nhs.digital.nhsconnect.nhais.exceptions.OperationOutcomeError;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OperationOutcomeExceptionHandlerTest extends ResponseEntityExceptionHandler {

    @Mock
    private FhirParser fhirParser;

    @InjectMocks
    private OperationOutcomeExceptionHandler exceptionHandler;

    @BeforeEach
    public void beforeEach() {
        when(fhirParser.encodeToString(any(OperationOutcome.class))).thenReturn("encoded operation outcome");
    }

    @Test
    public void testOperationOutcomeError() {
        StubOperationOutcomeError error = new StubOperationOutcomeError("the message");
        ResponseEntity<String> responseEntity = exceptionHandler.handleAllErrors(error, null);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("encoded operation outcome", responseEntity.getBody());
    }

    @Test
    public void testOtherErrors() {
        Exception error = new Exception("the message");
        ResponseEntity<String> responseEntity = exceptionHandler.handleAllErrors(error, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("encoded operation outcome", responseEntity.getBody());
    }

    private static class StubOperationOutcomeError extends Exception implements OperationOutcomeError {

        public StubOperationOutcomeError(String message) {
            super(message);
        }

        @Override
        public IBaseOperationOutcome getOperationOutcome() {
            return new OperationOutcome();
        }

        @Override
        public HttpStatus getStatusCode() {
            return HttpStatus.BAD_REQUEST;
        }
    }
}
