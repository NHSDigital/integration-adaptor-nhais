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
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonPreviousNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonSexMapper;
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

import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.mapSegment;
import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.optional;
import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.optionalGroup;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AcceptanceFirstTranslator implements FhirToEdifactTranslator {

    private final PartyQualifierMapper partyQualifierMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final AcceptanceCodeMapper acceptanceCodeMapper;
    private final AcceptanceTypeMapper acceptanceTypeMapper;
    private final AcceptanceDateMapper acceptanceDateMapper;
    private final PersonNameMapper personNameMapper;
    private final PersonPlaceOfBirthMapper personPlaceOfBirthMapper;
    private final PersonPreviousNameMapper personPreviousNameMapper;
    private final PersonSexMapper personSexMapper;
    private final PersonAddressMapper personAddressMapper;
    private final PersonDateOfBirthMapper personDateOfBirthMapper;
    private final DrugsMarkerMapper drugsMarkerMapper;
    private final FreeTextMapper freeTextMapper;
    private final ResidentialInstituteNameAndAddressMapper residentialInstituteNameAndAddressMapper;
    private final OptionalInputValidator validator;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        boolean nhsNumberIsMissing = validator.nhsNumberIsMissing(parameters);
        boolean placeOfBirthIsMissing = validator.placeOfBirthIsMissing(parameters);
        if(nhsNumberIsMissing && placeOfBirthIsMissing) {
            throw new FhirValidationException("Place of birth is mandatory when NHS number is missing");
        }

        return Stream.of(
            //BGM
            mapSegment(new BeginningOfMessage()),
            //NAD+FHS
            partyQualifierMapper,
            //DTM+137
            mapSegment(new DateTimePeriod(null, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP)),
            //RFF+950
            mapSegment(new ReferenceTransactionType(ReferenceTransactionType.Outbound.ACCEPTANCE)),
            //S01
            mapSegment(new SegmentGroup(1)),
            //RFF+TN
            mapSegment(new ReferenceTransactionNumber()),
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
            mapSegment(new SegmentGroup(2)),
            //PNA+PAT
            personNameMapper,
            //DTM+329
            optional(personDateOfBirthMapper, parameters),
            //PDI
            personSexMapper,
            //NAD+PAT
            personAddressMapper,
            //S02
            optionalGroup(new SegmentGroup(2), List.of(personPreviousNameMapper), parameters),
            //PNA+PER
            optional(personPreviousNameMapper, parameters))
            .map(mapper -> mapper.map(parameters))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}
