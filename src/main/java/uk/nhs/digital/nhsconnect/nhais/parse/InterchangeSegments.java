package uk.nhs.digital.nhsconnect.nhais.parse;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.Section;

public class InterchangeSegments {

    private final List<String> segments;

    public InterchangeSegments(List<String> segments) {
        this.segments = List.copyOf(segments);
    }

    public List<String> extract() {
        var firstMessageHeaderIndex = EdifactParserV2.findAllIndexes(segments, MessageHeader.KEY).get(0);
        var allMessageTrailerIndexes = EdifactParserV2.findAllIndexes(segments, MessageTrailer.KEY);
        var lastMessageTrailerIndex = allMessageTrailerIndexes.get(allMessageTrailerIndexes.size() - 1);

        return Stream.of(
            segments.subList(0, firstMessageHeaderIndex),
            segments.subList(lastMessageTrailerIndex + 1, segments.size()))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

}
