package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import java.util.Arrays;

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

    private String extractSegment(String[] header, String key) {
        return Arrays.stream(header)
            .map(String::strip)
            .filter(segment -> segment.startsWith(key))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }
}
