package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class UnknownWorkflowException extends NhaisBaseRuntimeException {
    public UnknownWorkflowException(Object workflowId) {
        super("Unknown workflow id: " + workflowId);
    }
}
