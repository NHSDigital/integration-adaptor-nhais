package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
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
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.service.JsonPatchTranslationItems;

import java.util.Arrays;
import java.util.List;

/**
 * @deprecated for removal after completion of last translate outbound transaction story. Useful to stub out APIs until
 * then
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentTranslator implements AmendmentToEdifactTranslator {

    @Override
    public List<Segment> translate(AmendmentBody amendmentBody) throws FhirValidationException {
        return Arrays.asList(
            new BeginningOfMessage(),
            new NameAndAddress(amendmentBody.getHealthcarePartyCode(), NameAndAddress.QualifierAndCode.FHS),
            new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP),
            new ReferenceTransactionType(ReferenceTransactionType.Outbound.AMENDMENT),
            new SegmentGroup(1),
            new ReferenceTransactionNumber(),
            new GpNameAndAddress(amendmentBody.getGpCode(), "900"),
            new SegmentGroup(2),
            PersonName.builder()
                    .nhsNumber(amendmentBody.getNhsNumber())
                    .patientIdentificationType(PersonName.PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
                    .build()
        );
    }

}
