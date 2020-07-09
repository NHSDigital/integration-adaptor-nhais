package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
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
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers.AmendmentAddressToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers.AmendmentNameToEdifactMapper;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers.AmendmentPreviousNameToEdifactMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentToEdifactTranslator {

    private final AmendmentNameToEdifactMapper amendmentNameToEdifactMapper;
    private final AmendmentPreviousNameToEdifactMapper amendmentPreviousNameToEdifactMapper;
    private final AmendmentAddressToEdifactMapper amendmentAddressToEdifactMapper;

    public List<Segment> translate(AmendmentBody amendmentBody) {
        var segments = new ArrayList<Segment>();
        segments.add(new BeginningOfMessage());
        segments.add(new NameAndAddress(amendmentBody.getHealthcarePartyCode(), NameAndAddress.QualifierAndCode.FHS));
        segments.add(new DateTimePeriod(DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP));
        segments.add(new ReferenceTransactionType(ReferenceTransactionType.Outbound.AMENDMENT));
        segments.add(new SegmentGroup(1));
        segments.add(new ReferenceTransactionNumber());
        segments.add(new GpNameAndAddress(amendmentBody.getGpCode(), "900"));
        segments.add(new SegmentGroup(2));
        segments.add(amendmentNameToEdifactMapper
            .map(amendmentBody)
            .orElseThrow(() -> new PatchValidationException(PersonName.class.getSimpleName() + " segment is mandatory")));
        amendmentAddressToEdifactMapper.map(amendmentBody).ifPresent(segments::add);
        segments.addAll(amendmentPreviousNameToEdifactMapper
            .map(amendmentBody)
            .map(previousNameSegments -> List.of(
                new SegmentGroup(2),
                previousNameSegments))
            .orElse(Collections.emptyList()));

        return segments;
    }
}
