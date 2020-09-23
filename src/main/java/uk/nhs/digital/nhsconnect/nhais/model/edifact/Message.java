package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Message extends Section {

    private static final String DEFAULT_GP_CODE = "9999";

    @Getter(lazy = true)
    private final MessageHeader messageHeader =
        MessageHeader.fromString(extractSegment(MessageHeader.KEY));
    @Getter(lazy = true)
    private final HealthAuthorityNameAndAddress healthAuthorityNameAndAddress =
        HealthAuthorityNameAndAddress.fromString(extractSegment(HealthAuthorityNameAndAddress.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final RegistrationTimestamp translationDateTime =
        RegistrationTimestamp.fromString(extractSegment(RegistrationTimestamp.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final RecepTimestamp recepTranslationDateTime =
        RecepTimestamp.fromString(extractSegment(RecepTimestamp.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final ReferenceTransactionType referenceTransactionType =
        ReferenceTransactionType.fromString(extractSegment(ReferenceTransactionType.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final List<ReferenceMessageRecep> referenceMessageReceps =
        extractSegments(ReferenceMessageRecep.KEY_QUALIFIER).stream()
            .map(ReferenceMessageRecep::fromString)
            .collect(Collectors.toList());
    @Getter(lazy = true)
    private final ReferenceInterchangeRecep referenceInterchangeRecep =
        ReferenceInterchangeRecep.fromString(extractSegment(ReferenceInterchangeRecep.KEY_QUALIFIER));

    @Getter
    @Setter
    private Interchange interchange;
    @Getter
    @Setter
    private List<Transaction> transactions;

    public Message(List<String> edifactSegments) {
        super(edifactSegments);
    }

    @Override
    protected Stream<Supplier<? extends Segment>> getSegmentsToValidate() {
        return Stream.of(
            (Supplier<? extends Segment>) this::getMessageHeader,
            (Supplier<? extends Segment>) this::getTranslationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Message{SIS: %s, SMS: %s}",
            getInterchange().getInterchangeHeader().getSequenceNumber(),
            getMessageHeader().getSequenceNumber());
    }

    public String findFirstGpCode() {
        return getTransactions()
            .stream()
            .limit(1)
            .map(transaction -> transaction.extractOptionalSegment(GpNameAndAddress.KEY_QUALIFIER))
            .flatMap(Optional::stream)
            .map(GpNameAndAddress::fromString)
            .map(GpNameAndAddress::getIdentifier)
            .findFirst()
            .orElse(DEFAULT_GP_CODE);
    }
}
