package uk.nhs.digital.nhsconnect.nhais.parse;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class FhirParserTest {

    private FhirParser fhirParser = new FhirParser();

    @Test
    public void when_validPatient_parsedSuccessfully() throws Exception {
        try(InputStream is = this.getClass().getResourceAsStream("/patient/patient.json")) {
            String payload = IOUtils.toString(is, StandardCharsets.UTF_8);
            fhirParser.parsePatient(payload);
        }
    }

    @Test
    public void when_emptyPatientPayload_throwsFhirValidationException() throws Exception {
        String payload = "{}";
        FhirValidationException ex = assertThrows(FhirValidationException.class, () -> fhirParser.parsePatient(payload));
        assertExceptionAndOperationOutcome(ex, "missing required element: 'resourceType'");
    }

    @Test
    public void when_invalidId_throwsExceptionWithOperationOutcome() throws Exception {
        try(InputStream is = this.getClass().getResourceAsStream("/patient/patient_invalid_id.json")) {
            String payload = IOUtils.toString(is, StandardCharsets.UTF_8);
            FhirValidationException ex = assertThrows(FhirValidationException.class, () -> fhirParser.parsePatient(payload));
            assertExceptionAndOperationOutcome(ex, "Expected SCALAR (STRING) and found SCALAR (NUMBER)");
        }
    }

    @Test
    public void when_missingId_parsedSuccessfully() throws Exception {
        try(InputStream is = this.getClass().getResourceAsStream("/patient/patient_missing_id.json")) {
            String payload = IOUtils.toString(is, StandardCharsets.UTF_8);
            fhirParser.parsePatient(payload);
        }
    }

    @Test
    public void when_multipleErrors_onlyFirstErrorAppearsInOperationOutcomeIssues() throws Exception {
        // We would like multiple errors to appear but this is a limitation of HAPI parsing
        // See comment in FhirParser about enhanced validation
        try(InputStream is = this.getClass().getResourceAsStream("/patient/patient_payload_multiple_invalid.json")) {
            String payload = IOUtils.toString(is, StandardCharsets.UTF_8);
            FhirValidationException ex = assertThrows(FhirValidationException.class, () -> fhirParser.parsePatient(payload));
            assertEquals(1, ((OperationOutcome)ex.getOperationOutcome()).getIssue().size());
            assertExceptionAndOperationOutcome(ex, "Expected SCALAR (STRING) and found SCALAR (NUMBER)");
        }
    }

    private void assertExceptionAndOperationOutcome(FhirValidationException ex, String expectedMessageFragment) {
        assertTrue(ex.getMessage().contains(expectedMessageFragment));
        OperationOutcome operationOutcome = (OperationOutcome) ex.getOperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent issue = operationOutcome.getIssueFirstRep();
        assertEquals(OperationOutcome.IssueSeverity.ERROR, issue.getSeverity());
        assertEquals(OperationOutcome.IssueType.STRUCTURE, issue.getCode());
        assertTrue(issue.getDetails().getText().contains(expectedMessageFragment));
    }

}
