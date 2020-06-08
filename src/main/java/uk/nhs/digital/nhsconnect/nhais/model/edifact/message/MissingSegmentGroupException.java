package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

public class MissingSegmentGroupException extends ToEdifactParsingException {

    public MissingSegmentGroupException(String message) {
        super(message);
    }
}
