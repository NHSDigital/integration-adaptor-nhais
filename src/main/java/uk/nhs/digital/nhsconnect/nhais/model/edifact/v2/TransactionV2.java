package uk.nhs.digital.nhsconnect.nhais.model.edifact.v2;

import lombok.Getter;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TransactionV2 extends Section {
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
    private MessageV2 message;

    public TransactionV2(List<String> segments) {
        super(segments);
    }

    @Override
    protected Stream<Supplier<? extends Segment>> getSegmentsToValidate() {
        return Stream.empty();
    }
}
