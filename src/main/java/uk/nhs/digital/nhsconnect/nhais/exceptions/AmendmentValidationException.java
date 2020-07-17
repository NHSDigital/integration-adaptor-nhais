package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class AmendmentValidationException extends BadRequestException {

    public AmendmentValidationException(String message) {
        super(message);
    }

}
