package uk.nhs.digital.nhsconnect.nhais.rest.exception;

import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FhirValidationExceptionTest {

    @Mock
    ValidationResult validationResult;

    @Mock
    IBaseOperationOutcome operationOutcome;

    @BeforeEach
    public void beforeEach() {
        when(validationResult.toOperationOutcome()).thenReturn(operationOutcome);
    }

    @Test
    public void testValidationResult_NoMessages() {
        when(validationResult.getMessages()).thenReturn(Collections.emptyList());
        FhirValidationException exception = new FhirValidationException(validationResult);
        assertEquals("JSON FHIR Resource failed validation", exception.getMessage());
    }

    @Test
    public void testValidationResult_SingleMessage() {
        SingleValidationMessage message = new SingleValidationMessage();
        message.setMessage("the message");
        when(validationResult.getMessages()).thenReturn(List.of(message));
        FhirValidationException exception = new FhirValidationException(validationResult);
        assertEquals("JSON FHIR Resource failed validation: the message", exception.getMessage());
    }

    @Test
    public void testValidationResult_MultipleMessages() {
        SingleValidationMessage message = new SingleValidationMessage();
        message.setMessage("the message");
        when(validationResult.getMessages()).thenReturn(Arrays.asList(message, message, message));
        FhirValidationException exception = new FhirValidationException(validationResult);
        assertEquals("JSON FHIR Resource failed validation: the message (and 2 more error messages truncated)", exception.getMessage());
    }

    @Test
    public void testWithoutValidationResult() {
        FhirValidationException ex = new FhirValidationException("the message");
        assertEquals("the message", ex.getMessage());
        OperationOutcome operationOutcome = (OperationOutcome) ex.getOperationOutcome();
        assertEquals(OperationOutcome.IssueType.STRUCTURE, operationOutcome.getIssueFirstRep().getCode());
    }

}
