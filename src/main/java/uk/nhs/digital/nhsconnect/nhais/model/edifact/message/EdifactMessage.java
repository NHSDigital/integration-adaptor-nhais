package uk.nhs.digital.nhsconnect.nhais.model.edifact.message;

import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.util.Arrays;
import java.util.Optional;

public class EdifactMessage {

    private final String edifactMessage;

    public EdifactMessage(@NonNull String edifactMessage) {
        this.edifactMessage = edifactMessage.replaceAll("\\n", "");
    }

    public InterchangeHeader getInterchangeHeader() {
        return InterchangeHeader.fromString(
            extractSegment(InterchangeHeader.KEY)
        );
    }

    public MessageHeader getMessageHeader() {
        return MessageHeader.fromString(
            extractSegment(MessageHeader.KEY)
        );
    }

    public ReferenceTransactionNumber getReferenceTransactionNumber() {
        return ReferenceTransactionNumber.fromString(
            extractSegment(ReferenceTransactionNumber.KEY_QUALIFIER)
        );
    }

    public ReferenceTransactionType getReferenceTransactionType() {
        return ReferenceTransactionType.fromString(
            extractSegment(ReferenceTransactionType.KEY_QUALIFIER)
        );
    }

    public HealthAuthorityNameAndAddress getHealthAuthorityNameAndAddress() {
        return HealthAuthorityNameAndAddress.fromString(
            extractSegment(HealthAuthorityNameAndAddress.KEY_QUALIFIER)
        );
    }

    public GpNameAndAddress getGpNameAndAddress() {
        return GpNameAndAddress.fromString(
            extractSegment(GpNameAndAddress.KEY_QUALIFIER)
        );
    }

    public DateTimePeriod getTranslationDateTime() {
        return DateTimePeriod.fromString(
            extractSegment(DateTimePeriod.KEY)
        );
    }

    public NameAndAddress getNameAndAddress() {
        return NameAndAddress.fromString(
            extractSegment(NameAndAddress.KEY)
        );
    }

    public FreeText getFreeText() {
        return FreeText.fromString(
            extractSegment(FreeText.KEY_QUALIFIER)
        );
    }

    public Optional<PatientIdentifier> getPatientIdentifier() {
        return extractOptionalSegment(PatientIdentifier.KEY_QUALIFIER)
            .map(PatientIdentifier::fromString);
    }

    public InterchangeTrailer getInterchangeTrailer() {
        return InterchangeTrailer.fromString(
            extractSegment(InterchangeTrailer.KEY)
        );
    }

    private String[] getSegments() {
        return Split.bySegmentTerminator(edifactMessage.strip());
    }

    private Optional<String> extractOptionalSegment(String key) {
        return Arrays.stream(getSegments())
            .map(String::strip)
            .filter(segment -> segment.startsWith(key))
            .findFirst();
    }

    private String extractSegment(String key) {
        return extractOptionalSegment(key)
            .orElseThrow(() -> new MissingSegmentException("EDIFACT message is missing segment " + key));
    }
}