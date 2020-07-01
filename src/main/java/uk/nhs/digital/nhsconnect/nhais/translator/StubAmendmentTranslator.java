package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.Amendment;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PatientName;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.util.Arrays;
import java.util.List;

/**
 * @deprecated for removal after completion of last translate outbound transaction story. Useful to stub out APIs until
 * then
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Deprecated
public class StubAmendmentTranslator implements AmendmentToEdifactTranslator {

    @Override
    public List<Segment> translate(Amendment amendment) throws FhirValidationException {
        return Arrays.asList(
            new BeginningOfMessage(),
            new NameAndAddress(amendment.getHealthcarePartyCode(), NameAndAddress.QualifierAndCode.FHS),
            new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP),
            new ReferenceTransactionType(ReferenceTransactionType.TransactionType.AMENDMENT),
            new SegmentGroup(1),
            new ReferenceTransactionNumber(),
            new GpNameAndAddress(amendment.getGpCode(), "900"),
            new SegmentGroup(2),
            PersonName.builder().nhsNumber(amendment.getNhsNumber()).build()
        );
    }

}
