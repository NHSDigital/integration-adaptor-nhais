package uk.nhs.digital.nhsconnect.nhais.outbound.jsonpatch;

import uk.nhs.digital.nhsconnect.nhais.rest.exception.BadRequestException;

class AmendmentValidationException extends BadRequestException {

    public AmendmentValidationException(String message) {
        super(message);
    }

}
