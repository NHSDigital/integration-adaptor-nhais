package uk.nhs.digital.nhsconnect.nhais.model.edifact.v2;

import lombok.Getter;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class InterchangeV2 extends Section {
    @Getter(lazy = true)
    private final InterchangeHeader interchangeHeader = InterchangeHeader.fromString(extractSegment(InterchangeHeader.KEY));
    @Getter(lazy = true)
    private final InterchangeTrailer interchangeTrailer = InterchangeTrailer.fromString(extractSegment(InterchangeTrailer.KEY));
    @Getter
    @Setter
    private List<MessageV2> messages;

    public InterchangeV2(List<String> edifactSegments) {
        super(edifactSegments);
    }

    @Override
    protected Stream<Supplier<? extends Segment>> getSegmentsToValidate() {
        return Stream.of(
            (Supplier<? extends Segment>) this::getInterchangeHeader,
            (Supplier<? extends Segment>) this::getInterchangeTrailer);
    }
}
