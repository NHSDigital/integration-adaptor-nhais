package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class EdifactValidationException extends RuntimeException {
    public EdifactValidationException(String message) {
        super(message);
    }
}
