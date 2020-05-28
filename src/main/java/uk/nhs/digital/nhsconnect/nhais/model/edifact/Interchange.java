package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class Interchange {

    // TODO: stub for the internal representation of an edifact interchange
    private final List<Segment> segments;

    public Interchange(List<Segment> segments) {
        this.segments = segments;
    }

    @Getter(lazy=true)
    private final InterchangeHeader interchangeHeader = findSegment(InterchangeHeader.class).orElseThrow();
    @Getter(lazy=true)
    private final MessageHeader messageHeader = findSegment(MessageHeader.class).orElseThrow();
    @Getter(lazy=true)
    private final ReferenceTransactionNumber referenceTransactionNumber = findSegment(ReferenceTransactionNumber.class).orElseThrow();
    @Getter(lazy=true)
    private final ReferenceTransactionType referenceTransactionType = findSegment(ReferenceTransactionType.class).orElseThrow();

    private <T extends Segment> Optional<T> findSegment(Class<T> klass) {
        return segments.stream()
            .filter(klass::isInstance)
            .map(klass::cast)
            .findFirst();
    }
}
