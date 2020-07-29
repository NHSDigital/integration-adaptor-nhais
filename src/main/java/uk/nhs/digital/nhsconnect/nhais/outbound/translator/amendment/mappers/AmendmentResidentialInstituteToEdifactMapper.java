package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ResidentialInstituteNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentResidentialInstituteToEdifactMapper extends AmendmentToEdifactMapper {

    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var code = amendmentBody.getJsonPatches().getResidentialInstituteCode()
            .map(AmendmentPatch::getAmendmentValue)
            .map(amendmentValue -> ObjectUtils.defaultIfNull(amendmentValue.get(), AmendmentPatch.REMOVE_INDICATOR))
            .orElseThrow(() -> new PatchValidationException("Missing Residential Institute Code value"));

        return ResidentialInstituteNameAndAddress.builder()
            .identifier(code)
            .build();
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return amendmentBody.getJsonPatches().getResidentialInstituteCode().isPresent();
    }

    @Override
    void validatePatches(JsonPatches patches) {
        patches.getResidentialInstituteCode()
            .filter(AmendmentPatch::isRemoval)
            .ifPresent(value -> {
                throw new PatchValidationException("Removing Residential Institute Code should be done using extension with 'null' value");
            });

        patches.getResidentialInstituteCode()
            .map(AmendmentPatch::getAmendmentValue)
            .map(AmendmentValue::get)
            .filter(StringUtils.EMPTY::equals) // can't use StringUtils.isBlank nor isEmpty as null is a valid value here
            .ifPresent(value -> {
                throw new PatchValidationException("String value must not be empty");
            });
    }
}
