package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentSexToEdifactMapper extends AmendmentToEdifactMapper {
    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var sexCode = amendmentBody.getJsonPatches().getSex()
            .map(AmendmentPatch::getValue)
            .map(AmendmentValue::get)
            .map(PersonSex.Gender::fromName)
            .orElseThrow(() -> new PatchValidationException("Missing " + JsonPatches.SEX_PATH));

        return PersonSex.builder()
            .gender(sexCode)
            .build();
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return amendmentBody.getJsonPatches().getSex().isPresent();
    }

    @Override
    void validatePatches(JsonPatches patches) {
        if (patches.getSex().isPresent()) {
            if (patches.getSex().get().getOp() == AmendmentPatchOperation.REMOVE) {
                throw new PatchValidationException("Illegal remove operation on " + JsonPatches.SEX_PATH);
            }
            if (StringUtils.isBlank(patches.getSex().get().getValue().get())) {
                throw new PatchValidationException("Invalid value for " + JsonPatches.SEX_PATH);
            }
        }
    }
}
