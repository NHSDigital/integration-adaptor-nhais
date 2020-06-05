package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

public class EdifactValidationException extends ToEdifactParsingException {
    public EdifactValidationException(String message)
    {
        super(message);
    }
}
