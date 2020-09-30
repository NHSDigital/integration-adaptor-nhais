package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Interchange extends Section {
    @Getter(lazy = true)
    private final InterchangeHeader interchangeHeader = InterchangeHeader.fromString(extractSegment(InterchangeHeader.KEY));
    @Getter(lazy = true)
    private final InterchangeTrailer interchangeTrailer = InterchangeTrailer.fromString(extractSegment(InterchangeTrailer.KEY));

    @Getter
    @Setter
    private List<Message> messages;

    public Interchange(List<String> edifactSegments) {
        super(edifactSegments);
    }

    @Override
    public String toString() {
        return String.format("Interchange{SIS: %s}",
            getInterchangeHeader().getSequenceNumber());
    }
}
