package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

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
    private final Optional<DeductionReasonCode> deductionReasonCode =
        extractOptionalSegment(DeductionReasonCode.KEY).map(DeductionReasonCode::fromString);
    @Getter(lazy = true)
    private final Optional<DeductionDate> deductionDate =
        extractOptionalSegment(DeductionDate.KEY_QUALIFIER).map(DeductionDate::fromString);
    @Getter(lazy = true)
    private final Optional<NewHealthAuthorityName> newHealthAuthorityName =
        extractOptionalSegment(NewHealthAuthorityName.KEY_QUALIFIER).map(NewHealthAuthorityName::fromString);
    @Getter(lazy = true)
    private final Optional<FP69ReasonCode> fp69ReasonCode =
        extractOptionalSegment(FP69ReasonCode.KEY_QUALIFIER).map(FP69ReasonCode::fromString);
    @Getter(lazy = true)
    private final Optional<FP69ExpiryDate> fp69ExpiryDate =
        extractOptionalSegment(FP69ExpiryDate.KEY_QUALIFIER).map(FP69ExpiryDate::fromString);
    @Getter(lazy = true)
    private final Optional<PersonDateOfBirth> personDateOfBirth =
        extractOptionalSegment(PersonDateOfBirth.KEY_QUALIFIER).map(PersonDateOfBirth::fromString);
    @Getter(lazy = true)
    private final Optional<PersonAddress> personAddress =
        extractOptionalSegment(PersonAddress.KEY_QUALIFIER).map(PersonAddress::fromString);
    @Getter(lazy = true)
    private final Optional<PersonPreviousName> personPreviousName =
        extractOptionalSegment(PersonPreviousName.KEY_QUALIFIER).map(PersonPreviousName::fromString);
    @Getter(lazy = true)
    private final Optional<PersonSex> gender =
        extractOptionalSegment(PersonSex.KEY).map(PersonSex::fromString);
    @Getter(lazy = true)
    private final Optional<DrugsMarker> drugsMarker =
        extractOptionalSegment(DrugsMarker.KEY_PREFIX).map(DrugsMarker::fromString);
    @Getter(lazy = true)
    private final Optional<ResidentialInstituteNameAndAddress> residentialInstitution =
        extractOptionalSegment(ResidentialInstituteNameAndAddress.KEY_QUALIFIER)
            .map(ResidentialInstituteNameAndAddress::fromString);

    @Getter
    @Setter
    private Message message;

    public Transaction(List<String> segments) {
        super(segments);
    }

    @Override
    public String toString() {
        return String.format("Transaction{SIS: %s, SMS: %s, TN: %s}",
            getMessage().getInterchange().getInterchangeHeader().getSequenceNumber(),
            getMessage().getMessageHeader().getSequenceNumber(),
            getReferenceTransactionNumber().getTransactionNumber());
    }
}
