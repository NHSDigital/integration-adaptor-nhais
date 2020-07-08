package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.Arrays;
import java.util.List;

/**
 * @deprecated for removal after completion of last translate outbound transaction story. Useful to stub out APIs until
 * then
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentTranslator extends AmendmentToEdifactTranslator {
    @Override
    protected List<Segment> mapAllPatches(JsonPatches patches) {
        return Arrays.asList(
            new BeginningOfMessage(),
            new NameAndAddress(patches.getAmendmentBody().getHealthcarePartyCode(), NameAndAddress.QualifierAndCode.FHS),
            new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP),
            new ReferenceTransactionType(ReferenceTransactionType.Outbound.AMENDMENT),
            new SegmentGroup(1),
            new ReferenceTransactionNumber(),
            new GpNameAndAddress(patches.getAmendmentBody().getGpCode(), "900"),
            new SegmentGroup(2),
            PersonName.builder()
                .nhsNumber(patches.getAmendmentBody().getNhsNumber())
                .patientIdentificationType(PersonName.PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
                .build()
        );
    }

}
