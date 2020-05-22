package uk.nhs.digital.nhsconnect.nhais.utils;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OperationOutcomeUtilsTest {

    @Test
    public void testCreateUnknownError() {
        OperationOutcome operationOutcome = OperationOutcomeUtils.createFromMessage("unknown error");
        OperationOutcome.OperationOutcomeIssueComponent issue = operationOutcome.getIssueFirstRep();
        assertEquals(OperationOutcome.IssueSeverity.ERROR, issue.getSeverity());
        assertEquals(OperationOutcome.IssueType.UNKNOWN, issue.getCode());
        assertEquals("unknown error", issue.getDetails().getText());
    }

    @Test
    public void testCreateErrorWithSpecificType() {
        OperationOutcome operationOutcome =
                OperationOutcomeUtils.createFromMessage("structure error", OperationOutcome.IssueType.STRUCTURE);
        OperationOutcome.OperationOutcomeIssueComponent issue = operationOutcome.getIssueFirstRep();
        assertEquals(OperationOutcome.IssueSeverity.ERROR, issue.getSeverity());
        assertEquals(OperationOutcome.IssueType.STRUCTURE, issue.getCode());
        assertEquals("structure error", issue.getDetails().getText());
    }
}
