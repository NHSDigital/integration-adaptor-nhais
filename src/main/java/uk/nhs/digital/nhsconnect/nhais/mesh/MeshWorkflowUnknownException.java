package uk.nhs.digital.nhsconnect.nhais.mesh;

import uk.nhs.digital.nhsconnect.nhais.rest.exception.NhaisBaseException;

public class MeshWorkflowUnknownException extends NhaisBaseException {
    public MeshWorkflowUnknownException(String message) {
        super(message);
    }
}
