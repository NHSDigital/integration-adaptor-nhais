package uk.nhs.digital.nhsconnect.nhais.exceptions;

import org.hl7.fhir.r4.model.OperationOutcome;

public class PatchValidationException extends BadRequestException {

    public PatchValidationException(String message) {
        super(message);
    }

    public PatchValidationException(Throwable cause) {
        super(cause);
    }

    @Override
    public OperationOutcome.IssueType getIssueType() {
        return OperationOutcome.IssueType.STRUCTURE;
    }

}
