package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentDateOfBirthToEdifactMapper extends AmendmentToEdifactMapper {

    private final TimestampService timestampService;

    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var dateOfBirth = amendmentBody.getJsonPatches().getBirthDate()
            .map(AmendmentPatch::getValue)
            .map(AmendmentValue::get)
            .map(timestampService::parseDate)
            .orElseThrow(() -> new PatchValidationException("Missing " + JsonPatches.BIRTH_DATE_PATH));

        return PersonDateOfBirth.builder()
            .timestamp(dateOfBirth)
            .build();
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return amendmentBody.getJsonPatches().getBirthDate().isPresent();
    }

    @Override
    void validatePatches(JsonPatches patches) {
        if (patches.getBirthDate().isPresent()) {
            if (patches.getBirthDate().get().getOp() == AmendmentPatchOperation.REMOVE) {
                throw new PatchValidationException("Illegal remove operation on " + JsonPatches.BIRTH_DATE_PATH);
            }
            if (StringUtils.isBlank(patches.getBirthDate().get().getValue().get())) {
                throw new PatchValidationException("Invalid value for " + JsonPatches.BIRTH_DATE_PATH);
            }
        }
    }
}
