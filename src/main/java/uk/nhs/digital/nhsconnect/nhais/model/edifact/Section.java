package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.MissingSegmentException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

}
