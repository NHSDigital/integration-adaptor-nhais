package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class SenderOrRecipientMissingException extends NhaisBaseException {
    public SenderOrRecipientMissingException(String message) {
        super(message);
    }
}
