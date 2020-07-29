package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

import uk.nhs.digital.nhsconnect.nhais.rest.exception.BadRequestException;

public class ToEdifactParsingException extends BadRequestException {

    public ToEdifactParsingException(String message) {
        super(message);
    }

}
