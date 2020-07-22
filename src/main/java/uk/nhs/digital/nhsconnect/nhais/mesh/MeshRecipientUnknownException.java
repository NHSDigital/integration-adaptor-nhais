package uk.nhs.digital.nhsconnect.nhais.mesh;

import uk.nhs.digital.nhsconnect.nhais.rest.exception.NhaisBaseException;

public class MeshRecipientUnknownException extends NhaisBaseException {
    public MeshRecipientUnknownException(String message) {
        super(message);
    }
}
