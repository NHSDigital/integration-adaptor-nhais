package uk.nhs.digital.nhsconnect.nhais.translator;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.List;

@Component
public class AcceptanceBirthTranslator implements FhirToEdifactTranslator {

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        /*
            return Stream.of(
        //BGM
        emptyMapper(new BeginningOfMessage()),
        //NAD+FHS
        partyQualifierMapper,
        // DTM+137
        // missing
        //S01
        emptyMapper(new SegmentGroup(1)),
        //RFF+TN
        emptyMapper(new ReferenceTransactionNumber()),
        //NAD+GP
        gpNameAndAddressMapper,
        //S02
        emptyMapper(new SegmentGroup(2)),
        // PNA+PAT
        personNameMapper,
        //DTM+329
        personDateOfBirthMapper,
        //PNI
        personSexMapper,
        //NAD+PAT
        personAddressMapper)
        .map(mapper -> mapper.map(parameters))
        .collect(Collectors.toList());
         */
        return null;
    }
}
