package uk.nhs.digital.nhsconnect.nhais.model.exception;

public class NhaisBaseException extends Exception {

    public NhaisBaseException(String message) {
        super(message);
    }

    public NhaisBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public NhaisBaseException(Throwable cause) {
        super(cause);
    }
}
