package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;

@Getter @RequiredArgsConstructor
@ToString
public class Interchange {

    @Getter(AccessLevel.NONE)
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

    public List<ToEdifactParsingException> validate() {

        return Stream.of((Supplier<? extends Segment>) this::getInterchangeHeader,
            (Supplier<? extends Segment>) this::getMessageHeader,
            (Supplier<? extends Segment>) this::getReferenceTransactionNumber,
            (Supplier<? extends Segment>) this::getTranslationDateTime,
            (Supplier<? extends Segment>) this::getReferenceTransactionType,
            (Supplier<? extends Segment>) this::getHealthAuthorityNameAndAddress,
            (Supplier<? extends Segment>) this::getGpNameAndAddress)
            .map(this::checkData)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }

    private Optional<ToEdifactParsingException> checkData(Supplier<? extends Segment> segment){
        try {
            segment.get().validate();
        } catch (ToEdifactParsingException ex) {
            return Optional.of(ex);
        }
        return Optional.empty();
    }
}
