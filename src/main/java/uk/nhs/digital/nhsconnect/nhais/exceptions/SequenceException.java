package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class SequenceException extends BadRequestException {
    public SequenceException(String message) {
        super(message);
    }
}
