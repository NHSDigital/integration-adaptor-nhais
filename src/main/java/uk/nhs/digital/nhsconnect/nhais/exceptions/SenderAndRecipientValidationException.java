package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class SenderAndRecipientValidationException extends RuntimeException {
    public SenderAndRecipientValidationException(String message) {
        super(message);
    }
}
