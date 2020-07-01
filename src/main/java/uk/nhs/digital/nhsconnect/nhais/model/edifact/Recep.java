package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Recep extends Section {
    private List<Segment> segments;

    public Recep(List<String> edifactSegments) {
        super(edifactSegments);
    }

    @Override
    protected Stream<Supplier<? extends Segment>> getSegmentsToValidate() {
        return null;
    }
}
