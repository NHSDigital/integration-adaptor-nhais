package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class SequenceException extends BadRequestException {
    public SequenceException(String message) {
        super(message);
    }

    public SequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SequenceException(Throwable cause) {
        super(cause);
    }
}
