package uk.nhs.digital.nhsconnect.nhais.translator.removal;

import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.emptyMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.translator.FhirToEdifactTranslator;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RemovalTranslator implements FhirToEdifactTranslator {

    private final PartyQualifierMapper partyQualifierMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final PersonNameMapper personNameMapper;
    private final FreeTextMapper freeTextMapper;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        return Stream.of(
            //BGM
            emptyMapper(new BeginningOfMessage()),
            //NAD+FHS
            partyQualifierMapper,
            //DTM+137
            emptyMapper(new DateTimePeriod(null, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP)),
            //RFF+950
            emptyMapper(new ReferenceTransactionType(ReferenceTransactionType.Outbound.REMOVAL)),
            //S01
            emptyMapper(new SegmentGroup(1)),
            //RFF+TN
            emptyMapper(new ReferenceTransactionNumber()),
            //NAD+GP
            gpNameAndAddressMapper,
            //FTX+RGI
            freeTextMapper,
            //S02
            emptyMapper(new SegmentGroup(2)),
            //PNA+PAT
            personNameMapper)
            .map(mapper -> mapper.map(parameters))
            .collect(Collectors.toList());
    }

}
