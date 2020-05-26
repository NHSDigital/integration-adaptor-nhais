package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class Interchange {
    private static final String TERMINATOR = "'";

    // TODO: stub for the internal representation of an edifact interchange
    @Singular
    private List<Segment> segments;

    @Override
    public String toString() {
        return segments.stream()
                .map(segment -> segment.getKey() + "+" + segment.getValue() + TERMINATOR)
                .collect(Collectors.joining("\n"));

    }
}
