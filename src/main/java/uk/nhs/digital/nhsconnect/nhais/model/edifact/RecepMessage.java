package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecepMessage {

    /**
     * Matches an apostrophe NOT preceded by a question mark
     */
    public static final String ROW_DELIMITER = "((?<!\\?)')";

    private final String[] rows;

    public RecepMessage(String recepMessage) {
        this.rows = recepMessage.strip().split(ROW_DELIMITER);
    }

    public InterchangeHeader getInterchangeHeader() {
        return extractSegments(InterchangeHeader.KEY)
            .map(InterchangeHeader::fromString)
            .findFirst()
            .orElseThrow();
    }

    public List<ReferenceMessageRecep> getReferenceMessageReceps() {
        return extractSegments(ReferenceMessageRecep.KEY_QUALIFIER)
            .map(ReferenceMessageRecep::fromString)
            .collect(Collectors.toList());
    }

    public ReferenceInterchangeRecep getReferenceInterchangeRecep() {
        return extractSegments(ReferenceInterchangeRecep.KEY_QUALIFIER)
            .map(ReferenceInterchangeRecep::fromString)
            .findFirst()
            .orElseThrow();
    }

    public DateTimePeriod getDateTimePeriod() {
        return extractSegments(DateTimePeriod.KEY)
            .map(DateTimePeriod::fromString)
            .findFirst()
            .orElseThrow();
    }

    private Stream<String> extractSegments(@NonNull String key) {
        return Arrays.stream(rows)
            .map(String::strip)
            .filter(segment -> segment.startsWith(key));
    }

    public String getEdifact() {
        return String.join(ROW_DELIMITER, this.rows);
    }
}