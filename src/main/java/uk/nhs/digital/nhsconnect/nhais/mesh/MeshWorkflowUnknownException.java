package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.rest.exception.NhaisBaseException;

@Getter
public class MeshWorkflowUnknownException extends NhaisBaseException {

    private final String workflowId;

    public MeshWorkflowUnknownException(String message, String workflowId) {
        super(message);
        this.workflowId = workflowId;
    }
}
