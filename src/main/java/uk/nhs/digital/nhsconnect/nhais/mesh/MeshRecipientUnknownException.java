package uk.nhs.digital.nhsconnect.nhais.mesh;

import uk.nhs.digital.nhsconnect.nhais.rest.exception.BadRequestException;

public class MeshRecipientUnknownException extends BadRequestException {
    public MeshRecipientUnknownException(String message) {
        super(message);
    }
}
