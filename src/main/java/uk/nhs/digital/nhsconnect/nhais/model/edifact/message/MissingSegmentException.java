package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

public class MissingSegmentException extends ToEdifactParsingException {

    public MissingSegmentException(String message) {
        super(message);
    }
}
