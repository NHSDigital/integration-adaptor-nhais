package uk.nhs.digital.nhsconnect.nhais.outbound.translator.removal;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PatientJsonPaths;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.FhirToEdifactTranslator;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.acceptance.OptionalInputValidator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FromFhirToEdifactMapper.mapSegment;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RemovalTranslator implements FhirToEdifactTranslator {

    private final PartyQualifierMapper partyQualifierMapper;
    private final GpNameAndAddressMapper gpNameAndAddressMapper;
    private final PersonNameMapper personNameMapper;
    private final FreeTextMapper freeTextMapper;
    private final OptionalInputValidator validator;

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        if (validator.nhsNumberIsMissing(parameters)) {
            throw new FhirValidationException("Patient resource property " + PatientJsonPaths.NHS_NUMBER_PATH + " (NHS Number) is required");
        }
        return Stream.of(
            //BGM
            mapSegment(new BeginningOfMessage()),
            //NAD+FHS
            partyQualifierMapper,
            //DTM+137
            mapSegment(new DateTimePeriod(null, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP)),
            //RFF+950
            mapSegment(new ReferenceTransactionType(ReferenceTransactionType.Outbound.REMOVAL)),
            //S01
            mapSegment(new SegmentGroup(1)),
            //RFF+TN
            mapSegment(new ReferenceTransactionNumber()),
            //NAD+GP
            gpNameAndAddressMapper,
            //FTX+RGI
            freeTextMapper,
            //S02
            mapSegment(new SegmentGroup(2)),
            //PNA+PAT
            personNameMapper)
            .map(mapper -> mapper.map(parameters))
            .collect(Collectors.toList());
    }

}
