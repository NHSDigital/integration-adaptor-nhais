package uk.nhs.digital.nhsconnect.nhais.translator.acceptance;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceCodeMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceDateMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceTypeMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.DrugsMarkerMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonDateOfBirthMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonPlaceOfBirthMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonSexMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PreviousPersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.ResidentialInstituteNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.translator.FhirToEdifactTranslator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.emptyMapper;
import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AcceptanceBirthTranslator implements FhirToEdifactTranslator {

    private final PartyQualifierMapper partyQualifierMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final AcceptanceCodeMapper acceptanceCodeMapper;
    private final AcceptanceTypeMapper acceptanceTypeMapper;
    private final AcceptanceDateMapper acceptanceDateMapper;
    private final PersonNameMapper personNameMapper;
    private final PreviousPersonNameMapper previousPersonNameMapper;
    private final PersonPlaceOfBirthMapper personPlaceOfBirthMapper;
    private final PersonSexMapper personSexMapper;
    private final PersonAddressMapper personAddressMapper;
    private final PersonDateOfBirthMapper personDateOfBirthMapper;
    private final DrugsMarkerMapper drugsMarkerMapper;
    private final FreeTextMapper freeTextMapper;
    private final ResidentialInstituteNameAndAddressMapper residentialInstituteNameAndAddressMapper;

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
            emptyMapper(new ReferenceTransactionType(ReferenceTransactionType.Outbound.ACCEPTANCE)),
            //S01
            emptyMapper(new SegmentGroup(1)),
            //RFF+TN
            emptyMapper(new ReferenceTransactionNumber()),
            //NAD+GP
            gpNameAndAddressMapper,
            //NAD+RIC
            optional(residentialInstituteNameAndAddressMapper, parameters),
            //HEA+ACD
            acceptanceCodeMapper,
            //HEA+ATP
            acceptanceTypeMapper,
            //HEA+DM
            optional(drugsMarkerMapper, parameters),
            //DTM+956
            acceptanceDateMapper,
            //LOC+950
            optional(personPlaceOfBirthMapper, parameters),
            //FTX+RGI
            optional(freeTextMapper, parameters),
            //S02
            emptyMapper(new SegmentGroup(2)),
            //PNA+PAT
            personNameMapper,
            //PNA+PER
            optional(previousPersonNameMapper, parameters),
            //DTM+329
            personDateOfBirthMapper,
            //PDI
            personSexMapper,
            //NAD+PAT
            personAddressMapper)
            .map(mapper -> mapper.map(parameters))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
