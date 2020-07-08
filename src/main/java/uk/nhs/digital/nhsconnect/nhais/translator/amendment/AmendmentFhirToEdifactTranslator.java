package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.BeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentFhirToEdifactTranslator {

    private final AmendmentNameToEdifactTranslator amendmentNameToEdifactTranslator;
    private final AmendmentPreviousNameToEdifactTranslator amendmentPreviousNameToEdifactTranslator;

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
        segments.addAll(amendmentNameToEdifactTranslator.mapAllPatches(amendmentBody.getJsonPatches()));

        var previousNameSegments = amendmentPreviousNameToEdifactTranslator.mapAllPatches(amendmentBody.getJsonPatches());
        if (!previousNameSegments.isEmpty()) {
            segments.add(new SegmentGroup(2));
            segments.addAll(previousNameSegments);
        }

        return segments;
    }
}
