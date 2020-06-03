package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class Interchange {

    private final EdifactMessage edifactMessage;

    @Getter(lazy=true)
    private final InterchangeHeader interchangeHeader = edifactMessage.getInterchangeHeader();
    @Getter(lazy=true)
    private final MessageHeader messageHeader = edifactMessage.getMessageHeader();
    @Getter(lazy=true)
    private final ReferenceTransactionNumber referenceTransactionNumber = edifactMessage.getReferenceTransactionNumber();
    @Getter(lazy=true)
    private final DateTimePeriod translationDateTime = edifactMessage.getTranslationDateTime();
    @Getter(lazy=true)
    private final ReferenceTransactionType referenceTransactionType = edifactMessage.getReferenceTransactionType();
    @Getter(lazy=true)
    private final HealthAuthorityNameAndAddress healthAuthorityNameAndAddress = edifactMessage.getHealthAuthorityNameAndAddress();
    @Getter(lazy=true)
    private final GpNameAndAddress gpNameAndAddress = edifactMessage.getGpNameAndAddress();
}
