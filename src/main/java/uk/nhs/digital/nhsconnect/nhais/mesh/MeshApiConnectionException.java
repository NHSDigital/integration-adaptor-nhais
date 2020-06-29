package uk.nhs.digital.nhsconnect.nhais.mesh;

import uk.nhs.digital.nhsconnect.nhais.exceptions.NhaisBaseException;

public class MeshApiConnectionException extends NhaisBaseException {
    public MeshApiConnectionException(String message) {
        super(message);
    }
}
