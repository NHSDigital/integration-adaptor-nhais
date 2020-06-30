package uk.nhs.digital.nhsconnect.nhais.translator;

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
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonSexMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifactMapper.emptyMapper;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AcceptanceFirstTranslator implements FhirToEdifactTranslator {

    private final PartyQualifierMapper partyQualifierMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final AcceptanceCodeMapper acceptanceCodeMapper;
    private final AcceptanceTypeMapper acceptanceTypeMapper;
    private final AcceptanceDateMapper acceptanceDateMapper;
    private final PersonNameMapper personNameMapper;
    private final PersonSexMapper personSexMapper;
    private final PersonAddressMapper personAddressMapper;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
//        //TODO: enforce place of birth mandatory when NHS number is missing
//        if(new PersonNameMapper().map(parameters).getNhsNumber().isEmpty()) {
//            Optional.ofNullable(new PersonPlaceOfBirthMapper().map(parameters).getLocation())
//                .orElseThrow(() -> new FhirValidationException("Location is mandatory when NHS number is missing"));
//        }

        List<Segment> segments = Stream.of(
            //BGM
            emptyMapper(new BeginningOfMessage()),
            //NAD+FHS
            partyQualifierMapper,
            //DTM+137
            emptyMapper(new DateTimePeriod(null, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP)),
            //RFF+950
            emptyMapper(new ReferenceTransactionType(ReferenceTransactionType.TransactionType.OUT_ACCEPTANCE)),
            //S01
            emptyMapper(new SegmentGroup(1)),
            //RFF+TN
            emptyMapper(new ReferenceTransactionNumber()),
            //NAD+GP
            gpNameAndAddressMapper,
            //HEA+ACD
            acceptanceCodeMapper,
            //HEA+ATP
            acceptanceTypeMapper,
            //DTM+956
            acceptanceDateMapper,
            //LOC+950
//            new PersonPlaceOfBirthMapper(), //TODO: for now place of birth is ignored
            //S02
            emptyMapper(new SegmentGroup(2)),
            // PNA+PAT
            personNameMapper,
            //PNI
            personSexMapper,
            //NAD+PAT
            personAddressMapper)
            .map(mapper -> mapper.map(parameters))
            .collect(Collectors.toList());

        return segments;
    }
}
