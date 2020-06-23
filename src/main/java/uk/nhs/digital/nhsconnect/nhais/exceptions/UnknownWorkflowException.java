package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class UnknownWorkflowException extends NhaisBaseException {
    public UnknownWorkflowException(Object workflowId) {
        super("Unknown workflow id: " + workflowId);
    }
}
