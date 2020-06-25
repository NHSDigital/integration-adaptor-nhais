package uk.nhs.digital.nhsconnect.nhais.model.edifact.v2;

import lombok.Getter;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageV2 extends Section {
    @Getter(lazy = true)
    private final MessageHeader messageHeader =
        MessageHeader.fromString(extractSegment(MessageHeader.KEY));
    @Getter(lazy = true)
    private final HealthAuthorityNameAndAddress healthAuthorityNameAndAddress =
        HealthAuthorityNameAndAddress.fromString(extractSegment(HealthAuthorityNameAndAddress.KEY_QUALIFIER));
    @Getter(lazy = true)
    private final DateTimePeriod translationDateTime =
        DateTimePeriod.fromString(extractSegment(DateTimePeriod.KEY));
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
    private InterchangeV2 interchange;
    @Getter
    @Setter
    private List<TransactionV2> transactions;

    public MessageV2(List<String> edifactSegments) {
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
}
