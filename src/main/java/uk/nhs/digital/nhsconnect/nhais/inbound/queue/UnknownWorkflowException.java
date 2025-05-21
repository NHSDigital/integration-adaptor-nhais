package uk.nhs.digital.nhsconnect.nhais.inbound.queue;

import uk.nhs.digital.nhsconnect.nhais.rest.exception.NhaisBaseException;

class UnknownWorkflowException extends NhaisBaseException {
    UnknownWorkflowException(Object workflowId) {
        super("Unknown workflow id: " + workflowId);
    }
}
