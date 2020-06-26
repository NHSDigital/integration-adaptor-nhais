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
    private final FreeText freeText =
        FreeText.fromString(extractSegment(FreeText.KEY_QUALIFIER));
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