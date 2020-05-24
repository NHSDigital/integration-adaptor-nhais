package uk.nhs.digital.nhsconnect.nhais.exceptions;

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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
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
    public void testNoMessages() {
        when(validationResult.getMessages()).thenReturn(Collections.emptyList());
        FhirValidationException exception = new FhirValidationException(validationResult);
        assertEquals("JSON FHIR Resource failed validation", exception.getMessage());
        assertEquals(operationOutcome, exception.getOperationOutcome());
    }

    @Test
    public void testSingleMessage() {
        SingleValidationMessage message = new SingleValidationMessage();
        message.setMessage("the message");
        when(validationResult.getMessages()).thenReturn(Collections.singletonList(message));
        FhirValidationException exception = new FhirValidationException(validationResult);
        assertEquals("JSON FHIR Resource failed validation: the message", exception.getMessage());
        assertEquals(operationOutcome, exception.getOperationOutcome());
    }

    @Test
    public void testMultipleMessages() {
        SingleValidationMessage message = new SingleValidationMessage();
        message.setMessage("the message");
        when(validationResult.getMessages()).thenReturn(Arrays.asList(message, message, message));
        FhirValidationException exception = new FhirValidationException(validationResult);
        assertEquals("JSON FHIR Resource failed validation: the message (and 2 more error messages truncated)", exception.getMessage());
        assertEquals(operationOutcome, exception.getOperationOutcome());
    }

    @Test
    public void testWithoutValidationResult() {
        FhirValidationException ex = new FhirValidationException("the message");
        assertEquals("the message", ex.getMessage());
        OperationOutcome operationOutcome = (OperationOutcome) ex.getOperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent issue = operationOutcome.getIssueFirstRep();
        assertEquals(OperationOutcome.IssueSeverity.ERROR, issue.getSeverity());
        assertEquals(OperationOutcome.IssueType.STRUCTURE, issue.getCode());
        assertEquals("the message", issue.getDetails().getText());
    }

}
