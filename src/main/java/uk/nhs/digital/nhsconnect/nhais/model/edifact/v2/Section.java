package uk.nhs.digital.nhsconnect.nhais.model.edifact.v2;

import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.MissingSegmentException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Section {
    @Getter
    private final List<String> edifactSegments;

    public Section(List<String> edifactSegments) {
        this.edifactSegments = edifactSegments;
    }

    protected List<String> extractSegments(String key) {
        return edifactSegments.stream()
            .map(String::strip)
            .filter(segment -> segment.startsWith(key))
            .collect(Collectors.toList());
    }

    protected Optional<String> extractOptionalSegment(String key) {
        return extractSegments(key).stream()
            .findFirst();
    }

    protected String extractSegment(String key) {
        return extractOptionalSegment(key)
            .orElseThrow(() -> new MissingSegmentException("EDIFACT section is missing segment " + key));
    }

    public List<ToEdifactParsingException> validate() {
        return getSegmentsToValidate()
            .map(this::checkData)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }

    protected abstract Stream<Supplier<? extends Segment>> getSegmentsToValidate();

    protected Optional<ToEdifactParsingException> checkData(Supplier<? extends Segment> segment) {
        try {
            segment.get().validate();
        } catch (ToEdifactParsingException ex) {
            return Optional.of(ex);
        }
        return Optional.empty();
    }
}
