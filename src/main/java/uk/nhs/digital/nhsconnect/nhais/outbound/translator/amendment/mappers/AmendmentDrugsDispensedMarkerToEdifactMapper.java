package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentDrugsDispensedMarkerToEdifactMapper extends AmendmentToEdifactMapper {

    private static final List<String> ALLOWED_VALUES = List.of("true", "false");

    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var drugsMarkerValue = amendmentBody.getJsonPatches().getDrugsDispensedMarker()
            .map(AmendmentPatch::getAmendmentValue)
            .map(AmendmentValue::get)
            .map(Boolean::parseBoolean)
            .orElseThrow(() -> new PatchValidationException("Missing Drugs Dispensed Marker value"));

        return new DrugsMarker(drugsMarkerValue);
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return amendmentBody.getJsonPatches().getDrugsDispensedMarker().isPresent();
    }

    @Override
    void validatePatches(JsonPatches patches) {
        validateNonEmptyValues(List.of(patches.getDrugsDispensedMarker()));

        patches.getDrugsDispensedMarker()
            .filter(AmendmentPatch::isRemoval)
            .ifPresent(value -> {
                throw new PatchValidationException("Removing Drugs Dispensed Marker should be done using extension with 'false' value");
            });

        patches.getDrugsDispensedMarker()
            .filter(patch -> !ALLOWED_VALUES.contains(patch.getAmendmentValue().get()))
            .ifPresent(value -> {
                throw new PatchValidationException("Drugs Dispensed Marker must be one of " + ALLOWED_VALUES);
            });
    }
}
