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

import java.util.ArrayList;
import java.util.List;

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
        List<Segment> segments = new ArrayList<>();
        //BGM+++507'
        segments.add(new BeginningOfMessage());
        //NAD+FHS+XX1:954'
        segments.add(partyQualifierMapper.map(parameters));
        //DTM+137:199201151723:203'
        segments.add(acceptanceDateMapper.map(parameters));
        //RFF+950:G1'
        segments.add(new ReferenceTransactionType(ReferenceTransactionType.TransactionType.ACCEPTANCE));
        //S01+1'
        segments.add(new SegmentGroup(1));
        //RFF+TN:20'
        segments.add(new ReferenceTransactionNumber());
        //NAD+GP+2750922,295:900'
        segments.add(gpNameAndAddressMapper.map(parameters));
        //HEA+ACD+A:ZZZ'
        segments.add(acceptanceCodeMapper.map(parameters));
        //HEA+ATP+4:ZZZ'
        segments.add(acceptanceTypeMapper.map(parameters));
        //DTM+956:19920115:102' (acceptance date ?)
        segments.add(new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        //DTM+957:19910806:102'
        segments.add(personDateOfEntryMapper.map(parameters));
        //DTM+958:19680305:102' (optional)
        //date of exit
        //LOC+950+LANCASHIRE' (optional)
        //birth place
        //S02+2'
        segments.add(new SegmentGroup(2));
        //PNA+PAT++++SU:HOWES+FO:ALISON+TI:MRS+MI:J'
        segments.add(personNameMapper.map(parameters));
        //DTM+329:19651212:102'
        segments.add(personDateOfBirthMapper.map(parameters));
        //PDI+2'
        segments.add(personSexMapper.map(parameters));
        //NAD+PAT++:13 FOX CRESCENT::BROMLEY:KENT+++++BR1  7TQ'
        segments.add(personAddressMapper.map(parameters));

        return segments;
    }
}
