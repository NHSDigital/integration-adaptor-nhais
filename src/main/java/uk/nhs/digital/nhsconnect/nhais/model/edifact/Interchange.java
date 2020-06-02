package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Interchange {

    // TODO: stub for the internal representation of an edifact interchange
    private final List<Segment> segments;

    public Interchange(List<Segment> segments) {
        this.segments = ImmutableList.copyOf(segments);
    }

    @Getter(lazy=true)
    private final InterchangeHeader interchangeHeader = findSegment(InterchangeHeader.class).orElseThrow();
    @Getter(lazy=true)
    private final MessageHeader messageHeader = findSegment(MessageHeader.class).orElseThrow();
    @Getter(lazy=true)
    private final DateTimePeriod dateTimePeriod = findSegment(DateTimePeriod.class).orElseThrow();
    @Getter(lazy=true)
    private final ReferenceTransactionNumber referenceTransactionNumber = findSegment(ReferenceTransactionNumber.class).orElseThrow();
    @Getter(lazy=true)
    private final ReferenceTransactionType referenceTransactionType = findSegment(ReferenceTransactionType.class).orElseThrow();
    @Getter(lazy=true)
    private final List<ReferenceMessageRecep> referenceMessageReceps = findMultipleSegments(ReferenceMessageRecep.class);

    private <T extends Segment> Optional<T> findSegment(Class<T> klass) {
        return findMultipleSegments(klass).stream()
            .findFirst();
    }

    private <T extends Segment> List<T> findMultipleSegments(Class<T> klass) {
        return segments.stream()
            .filter(klass::isInstance)
            .map(klass::cast)
            .collect(Collectors.toList());
    }
}
