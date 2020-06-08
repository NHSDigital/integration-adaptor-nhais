package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

import java.util.Arrays;

import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

public class EdifactMessage {

    /**
     * Matches "S0" + any digit
     */
    private static final String SEGMENT_DELIMITER = "S0\\d\\+";
    /**
     * Matches an apostrophe NOT preceded by a question mark
     */
    private static final String ROW_DELIMITER = "((?<!\\?)')";

    private final String edifactMessage;

    public EdifactMessage(@NonNull String edifactMessage) {
        this.edifactMessage = edifactMessage.replaceAll("\\n", "");
    }

    public InterchangeHeader getInterchangeHeader() {
        return InterchangeHeader.fromString(
            extractSegment(getHeader(), InterchangeHeader.KEY)
        );
    }

    public MessageHeader getMessageHeader() {
        return MessageHeader.fromString(
            extractSegment(getHeader(), MessageHeader.KEY)
        );
    }

    public ReferenceTransactionNumber getReferenceTransactionNumber() {
        return ReferenceTransactionNumber.fromString(
            extractSegment(getFirstGroup(), ReferenceTransactionNumber.KEY_QUALIFIER)
        );
    }

    public ReferenceTransactionType getReferenceTransactionType() {
        return ReferenceTransactionType.fromString(
            extractSegment(getHeader(), ReferenceTransactionType.KEY_QUALIFIER)
        );
    }

    public HealthAuthorityNameAndAddress getHealthAuthorityNameAndAddress() {
        return HealthAuthorityNameAndAddress.fromString(
            extractSegment(getHeader(), HealthAuthorityNameAndAddress.KEY_QUALIFIER)
        );
    }

    public GpNameAndAddress getGpNameAndAddress() {
        return GpNameAndAddress.fromString(
            extractSegment(getFirstGroup(), GpNameAndAddress.KEY_QUALIFIER)
        );
    }

    public DateTimePeriod getTranslationDateTime() {
        return DateTimePeriod.fromString(
            extractSegment(getHeader(), DateTimePeriod.KEY)
        );
    }

    private String[] getHeader() {
        return edifactMessage.strip().split(SEGMENT_DELIMITER)[0].split(ROW_DELIMITER);
    }

    private String[] getFirstGroup() {
        String[] segmentSplit = edifactMessage.strip().split(SEGMENT_DELIMITER);
        if(segmentSplit.length < 2) {
            throw new MissingSegmentGroupException("First segment group is missing in " + Arrays.toString(segmentSplit));
        }
        return ("S01+"+segmentSplit[1]).split(ROW_DELIMITER);
    }

    private String[] getSecondGroup() {
        String[] input = edifactMessage.strip().split(SEGMENT_DELIMITER);
        if(input.length < 3) {
            throw new MissingSegmentGroupException("Second segment group is missing in " + edifactMessage);
        }
        return ("S02+"+input[2]).split(ROW_DELIMITER);
    }

    private String extractSegment(String[] segmentGroup, String key) {
        return Arrays.stream(segmentGroup)
            .map(String::strip)
            .filter(segment -> segment.startsWith(key))
            .findFirst()
            .orElseThrow(() -> new MissingSegmentException("Segment group " + Arrays.toString(segmentGroup) + " is missing segment " + key));
    }
}
