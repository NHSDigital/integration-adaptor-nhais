package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentPreviousNameToEdifactMapper extends AmendmentToEdifactMapper {

    @Override
    protected Optional<Segment> mapPatches(JsonPatches patches) {
        if (shouldCreatePersonPreviousNameSegment(patches)) {
            var personPreviousName = PersonPreviousName.builder()
                .previousFamilyName(patches.getPreviousSurname().map(this::getValue).orElse(null))
                .build();

            return Optional.of(personPreviousName);
        }
        return Optional.empty();
    }

    private boolean shouldCreatePersonPreviousNameSegment(JsonPatches patches) {
        return patches.getPreviousSurname().isPresent();
    }

    @Override
    protected void validatePatches(JsonPatches patches) {
        super.validatePatches(patches);

        validateNonEmptyValues(List.of(patches.getPreviousSurname()));
    }
}
