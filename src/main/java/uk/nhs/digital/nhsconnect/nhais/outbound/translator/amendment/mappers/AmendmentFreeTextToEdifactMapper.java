package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentFreeTextToEdifactMapper extends AmendmentToEdifactMapper {
    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        return new FreeText(amendmentBody.getFreeText());
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return StringUtils.isNotBlank(amendmentBody.getFreeText());
    }

    @Override
    void validatePatches(JsonPatches patches) {
        // NOP
    }
}
