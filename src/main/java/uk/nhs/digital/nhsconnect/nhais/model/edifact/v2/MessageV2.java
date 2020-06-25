package uk.nhs.digital.nhsconnect.nhais.model.edifact.v2;

import lombok.Getter;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.HealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.parse.TransactionSegments;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MessageV2 extends Section {
    @Getter
    @Setter
    private InterchangeV2 interchange;
    @Getter
    @Setter
    private List<TransactionV2> transactions;

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

    public MessageV2(InterchangeV2 interchange, List<String> messageEdifactSegmentsOnly, TransactionSegments transactionSegments) {
        super(messageEdifactSegmentsOnly);
        this.transactions = transactionSegments.toTransactions();
        this.interchange = interchange;
    }

    @Override
    protected Stream<Supplier<? extends Segment>> getSegmentsToValidate() {
        return Stream.of(
            (Supplier<? extends Segment>) this::getMessageHeader,
            (Supplier<? extends Segment>) this::getTranslationDateTime);
    }
}
