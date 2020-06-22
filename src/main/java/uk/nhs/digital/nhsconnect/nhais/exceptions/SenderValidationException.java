package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class SenderValidationException extends RuntimeException {
    public SenderValidationException(String message) {
        super(message);
    }
}
