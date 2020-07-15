package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Transaction extends Section {
    @Getter(lazy = true)
    private final ReferenceTransactionNumber referenceTransactionNumber =
        ReferenceTransactionNumber.fromString(extractSegment(ReferenceTransactionNumber.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final GpNameAndAddress gpNameAndAddress =
        GpNameAndAddress.fromString(extractSegment(GpNameAndAddress.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final Optional<PersonName> personName =
        extractOptionalSegment(PersonName.KEY_QUALIFIER).map(PersonName::fromString);
    @Getter(lazy = true)
    private final Optional<FreeText> freeText =
        extractOptionalSegment(FreeText.KEY_QUALIFIER).map(FreeText::fromString);
    @Getter(lazy = true)
    private final Optional<FP69ReasonCode> fp69ReasonCode =
        extractOptionalSegment(FP69ReasonCode.KEY_QUALIFIER).map(FP69ReasonCode::fromString);
    @Getter(lazy = true)
    private final FP69ExpiryDate fp69ExpiryDate =
        FP69ExpiryDate.fromString(extractSegment(FP69ExpiryDate.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final Optional<PersonDateOfBirth> personDateOfBirth =
        extractOptionalSegment(PersonDateOfBirth.KEY_QUALIFIER).map(PersonDateOfBirth::fromString);
    @Getter(lazy = true)
    private final Optional<PersonAddress> personAddress =
        extractOptionalSegment(PersonAddress.KEY_QUALIFIER).map(PersonAddress::fromString);

    @Getter
    @Setter
    private Message message;

    public Transaction(List<String> segments) {
        super(segments);
    }

    @Override
    protected Stream<Supplier<? extends Segment>> getSegmentsToValidate() {
        return Stream.empty();
    }

    @Override
    public String toString() {
        return String.format("Transaction{SIS: %s, SMS: %s, TN: %s}",
            getMessage().getInterchange().getInterchangeHeader().getSequenceNumber(),
            getMessage().getMessageHeader().getSequenceNumber(),
            getReferenceTransactionNumber().getTransactionNumber());
    }
}
