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
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonDateOfBirthMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonDateOfEntryMapper;
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
public class AcceptanceImmigrantTranslator implements FhirToEdifactTranslator {
    private final PartyQualifierMapper partyQualifierMapper;
    private final AcceptanceDateMapper acceptanceDateMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final AcceptanceCodeMapper acceptanceCodeMapper;
    private final AcceptanceTypeMapper acceptanceTypeMapper;
    private final PersonDateOfEntryMapper personDateOfEntryMapper;
    private final PersonNameMapper personNameMapper;
    private final PersonDateOfBirthMapper personDateOfBirthMapper;
    private final PersonSexMapper personSexMapper;
    private final PersonAddressMapper personAddressMapper;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        return Stream.of(
            //BGM+++507'
            emptyMapper(new BeginningOfMessage()),
            //NAD+FHS+XX1:954'
            partyQualifierMapper,
            //DTM+956:19920115:102' (acceptance date)
            emptyMapper(new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP)),
            //RFF+950:G1'
            emptyMapper(new ReferenceTransactionType(ReferenceTransactionType.TransactionType.OUT_ACCEPTANCE)),
            //S01+1'
            emptyMapper(new SegmentGroup(1)),
            //RFF+TN:20'
            emptyMapper(new ReferenceTransactionNumber()),
            //NAD+GP+2750922,295:900'
            gpNameAndAddressMapper,
            //HEA+ACD+A:ZZZ'
            acceptanceCodeMapper,
            //HEA+ATP+4:ZZZ'
            acceptanceTypeMapper,
            //DTM+137:199201151723:203'
            acceptanceDateMapper,
            //DTM+957:19910806:102'
            personDateOfEntryMapper,
            //DTM+958:19680305:102' (optional)
            //date of exit
            //LOC+950+LANCASHIRE' (optional)
            //birth place
            //S02+2'
            emptyMapper(new SegmentGroup(2)),
            //PNA+PAT++++SU:HOWES+FO:ALISON+TI:MRS+MI:J'
            personNameMapper,
            //PDI+2'
            personSexMapper,
            //NAD+PAT++:13 FOX CRESCENT::BROMLEY:KENT+++++BR1  7TQ'
            personAddressMapper)
            .map(mapper -> mapper.map(parameters))
            .collect(Collectors.toList());
    }
}
