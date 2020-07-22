package uk.nhs.digital.nhsconnect.nhais.sequence;

import uk.nhs.digital.nhsconnect.nhais.rest.exception.BadRequestException;

public class SequenceException extends BadRequestException {
    public SequenceException(String message) {
        super(message);
    }
}
