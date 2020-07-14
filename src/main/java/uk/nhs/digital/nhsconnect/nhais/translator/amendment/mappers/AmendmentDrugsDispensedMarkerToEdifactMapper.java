package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
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
            .map(AmendmentPatch::getValue)
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
        patches.getDrugsDispensedMarker()
            .filter(patch -> patch.getOp() == AmendmentPatchOperation.REMOVE)
            .ifPresent(value -> {
                throw new PatchValidationException("Removing Drugs Dispensed Marker should be done using extension with 'false' value");
            });

        if (patches.getDrugsDispensedMarker().isPresent()) {
            if (StringUtils.isBlank(patches.getDrugsDispensedMarker().get().getValue().get())) {
                throw new PatchValidationException("Boolean value of Drugs Dispensed Marker must not be empty");
            }
            if (!ALLOWED_VALUES.contains(patches.getDrugsDispensedMarker().get().getValue().get())) {
                throw new PatchValidationException("Drugs Dispensed Marker must be one of " + ALLOWED_VALUES);
            }
        }
    }
}
