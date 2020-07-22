package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentPreviousNameToEdifactMapper extends AmendmentToEdifactMapper {

    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var patches = amendmentBody.getJsonPatches();

        return PersonPreviousName.builder()
            .familyName(patches.getPreviousSurname()
                .map(AmendmentPatch::getFormattedSimpleValue)
                .orElse(null))
            .build();
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return amendmentBody.getJsonPatches().getPreviousSurname().isPresent();
    }

    @Override
    void validatePatches(JsonPatches patches) {
        validateNonEmptyValues(List.of(patches.getPreviousSurname()));
    }
}
