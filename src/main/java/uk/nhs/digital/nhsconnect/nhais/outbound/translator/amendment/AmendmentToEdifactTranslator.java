package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.PatchValidationException;
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
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentAddressToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentDateOfBirthToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentDrugsDispensedMarkerToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentFreeTextToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentNameToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentPreviousNameToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentResidentialInstituteToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers.AmendmentSexToEdifactMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentToEdifactTranslator {

    private final AmendmentNameToEdifactMapper nameToEdifactMapper;
    private final AmendmentPreviousNameToEdifactMapper previousNameToEdifactMapper;
    private final AmendmentAddressToEdifactMapper addressToEdifactMapper;
    private final AmendmentFreeTextToEdifactMapper freeTextToEdifactMapper;
    private final AmendmentDateOfBirthToEdifactMapper dateOfBirthToEdifactMapper;
    private final AmendmentSexToEdifactMapper sexToEdifactMapper;
    private final AmendmentResidentialInstituteToEdifactMapper residentialInstituteToEdifactMapper;
    private final AmendmentDrugsDispensedMarkerToEdifactMapper drugsDispensedMarkerToEdifactMapper;

    public List<Segment> translate(AmendmentBody amendmentBody) {
        var segments = new ArrayList<Segment>();
        segments.add(new BeginningOfMessage());
        segments.add(new NameAndAddress(amendmentBody.getHealthcarePartyCode(), NameAndAddress.QualifierAndCode.FHS));
        segments.add(new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        segments.add(new ReferenceTransactionType(ReferenceTransactionType.Outbound.AMENDMENT));
        segments.add(new SegmentGroup(1));
        segments.add(new ReferenceTransactionNumber());
        segments.add(new GpNameAndAddress(amendmentBody.getGpCode(), "900"));
        residentialInstituteToEdifactMapper.map(amendmentBody).ifPresent(segments::add);
        drugsDispensedMarkerToEdifactMapper.map(amendmentBody).ifPresent(segments::add);
        freeTextToEdifactMapper.map(amendmentBody).ifPresent(segments::add);
        segments.add(new SegmentGroup(2));
        segments.add(nameToEdifactMapper
            .map(amendmentBody)
            .orElseThrow(() -> new PatchValidationException(PersonName.class.getSimpleName() + " segment is mandatory")));
        dateOfBirthToEdifactMapper.map(amendmentBody).ifPresent(segments::add);
        sexToEdifactMapper.map(amendmentBody).ifPresent(segments::add);
        addressToEdifactMapper.map(amendmentBody).ifPresent(segments::add);
        segments.addAll(previousNameToEdifactMapper
            .map(amendmentBody)
            .map(previousNameSegments -> List.of(
                new SegmentGroup(2),
                previousNameSegments))
            .orElse(Collections.emptyList()));

        return segments;
    }
}
