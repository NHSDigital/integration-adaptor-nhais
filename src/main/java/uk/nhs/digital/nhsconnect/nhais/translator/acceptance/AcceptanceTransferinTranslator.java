package uk.nhs.digital.nhsconnect.nhais.translator.acceptance;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceCodeMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceDateMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceTypeMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonOldAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonSexMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PreviousGpNameMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.translator.FhirToEdifactTranslator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.mapSegment;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AcceptanceTransferinTranslator implements FhirToEdifactTranslator {

    private final PartyQualifierMapper partyQualifierMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final PreviousGpNameMapper previousGpNameMapper;
    private final AcceptanceCodeMapper acceptanceCodeMapper;
    private final AcceptanceTypeMapper acceptanceTypeMapper;
    private final AcceptanceDateMapper acceptanceDateMapper;
    private final PersonNameMapper personNameMapper;
    private final PersonSexMapper personSexMapper;
    private final PersonAddressMapper personAddressMapper;
    private final PersonOldAddressMapper personOldAddressMapper;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        return Stream.of(
            mapSegment(new BeginningOfMessage()),
            partyQualifierMapper,
            mapSegment(new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP)),
            mapSegment(new ReferenceTransactionType(ReferenceTransactionType.Outbound.ACCEPTANCE)),
            mapSegment(new SegmentGroup(1)),
            mapSegment(new ReferenceTransactionNumber()),
            gpNameAndAddressMapper,
            previousGpNameMapper,
            acceptanceCodeMapper,
            acceptanceTypeMapper,
            acceptanceDateMapper,
            mapSegment(new SegmentGroup(2)),
            personNameMapper,
            personSexMapper,
            personAddressMapper,
            personOldAddressMapper)
            .map(mapper -> mapper.map(parameters))
            .collect(Collectors.toList());
    }
}
