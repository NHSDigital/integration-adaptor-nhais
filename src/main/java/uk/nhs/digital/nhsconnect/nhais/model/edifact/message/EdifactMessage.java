package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class EdifactMessage {

    /**
     * Matches "S0" + any digit
     */
    private static final String SEGMENT_DELIMITER = "S0\\d\\+";
    /**
     * Matches an apostrophe NOT preceded by a question mark
     */
    private static final String ROW_DELIMITER = "((?<!\\?)')";

    private final String[] header;
    private final String[] firstGroup;
    private final String[] secondGroup;

    public EdifactMessage(String edifactMessage) {
        String[] input = edifactMessage.strip().split(SEGMENT_DELIMITER);
        this.header = input[0].split(ROW_DELIMITER);
        this.firstGroup = ("S01+"+input[1]).split(ROW_DELIMITER);
        this.secondGroup = ("S02+"+input[2]).split(ROW_DELIMITER);
    }

    public InterchangeHeader getInterchangeHeader() {
        return InterchangeHeader.fromString(
            extractSegment(header, InterchangeHeader.KEY)
        );
    }

    public MessageHeader getMessageHeader() {
        return MessageHeader.fromString(
            extractSegment(header, MessageHeader.KEY)
        );
    }

    public ReferenceTransactionNumber getReferenceTransactionNumber() {
        return ReferenceTransactionNumber.fromString(
            extractSegment(firstGroup, ReferenceTransactionNumber.KEY_QUALIFIER)
        );
    }

    public ReferenceTransactionType getReferenceTransactionType() {
        return ReferenceTransactionType.fromString(
            extractSegment(header, ReferenceTransactionType.KEY_QUALIFIER)
        );
    }

    public HealthAuthorityNameAndAddress getHealthAuthorityNameAndAddress() {
        return HealthAuthorityNameAndAddress.fromString(
            extractSegment(header, HealthAuthorityNameAndAddress.KEY_QUALIFIER)
        );
    }

    public GpNameAndAddress getGpNameAndAddress() {
        return GpNameAndAddress.fromString(
            extractSegment(firstGroup, GpNameAndAddress.KEY_QUALIFIER)
        );
    }

    public DateTimePeriod getTranslationDateTime() {
        return DateTimePeriod.fromString(
            extractSegment(header, DateTimePeriod.KEY)
        );
    }

    public NameAndAddress getNameAndAddress() {
        return NameAndAddress.fromString(
            extractSegment(header, NameAndAddress.KEY)
        );
    }

    public InterchangeTrailer getInterchangeTrailer() {
        return InterchangeTrailer.fromString(
            extractSegment(secondGroup, InterchangeTrailer.KEY)
        );
    }

    private String extractSegment(String[] header, String key) {
        var segment = extractSegments(header, key);
        if (segment.isEmpty()) {
            throw new NoSuchElementException("Missing segment for key " + key);
        } else if (segment.size() > 1) {
            throw new IllegalStateException("There are more than 1 segments for key " + key);
        }
        return segment.get(0);
    }

    private List<String> extractSegments(String[] header, String key) {
        return Arrays.stream(header)
            .map(String::strip)
            .filter(segment -> segment.startsWith(key))
            .collect(Collectors.toList());
    }
}
