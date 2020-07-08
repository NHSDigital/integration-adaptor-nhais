package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentNameToEdifactTranslator extends AmendmentToEdifactTranslator {
    @Override
    protected List<Segment> mapAllPatches(JsonPatches patches) {
        if (shouldCreatePersonNameSegment(patches)) {
            var title = patches.getTitle()
                .map(this::getValue)
                .orElse(null);
            var surname = patches.getSurname()
                .map(this::getValue)
                .orElse(null);
            var firstName = patches.getFirstForename()
                .or(() -> patches
                    .getAllForenamesPath()
                    .filter(patch -> patch.getOp() == AmendmentPatchOperation.REMOVE))
                .map(this::getValue)
                .orElse(null);
            var secondName = patches.getSecondForename()
                .map(this::getValue)
                .orElse(null);
            var otherName = patches.getOtherForenames()
                .map(this::getValue)
                .orElse(null);

            var personName = PersonName.builder()
                .nhsNumber(patches.getAmendmentBody().getNhsNumber())
                .title(title)
                .familyName(surname)
                .forename(firstName)
                .middleName(secondName)
                .thirdForename(otherName)
                .build();
            return Collections.singletonList(personName);
        }
        return Collections.emptyList();
    }

    private boolean shouldCreatePersonNameSegment(JsonPatches patches) {
        return Stream.of(
            patches.getTitle(),
            patches.getSurname(),
            patches.getFirstForename(),
            patches.getSecondForename(),
            patches.getOtherForenames(),
            patches.getAllForenamesPath())
            .anyMatch(Optional::isPresent);
    }

    @Override
    protected void validatePatches(JsonPatches patches) {
        super.validatePatches(patches);

        validateNonEmptyValues(List.of(
            patches.getTitle(),
            patches.getSurname(),
            patches.getFirstForename(),
            patches.getSecondForename(),
            patches.getOtherForenames()));
        validateFamilyNameRemoval(patches);
        validateGivenNameRemoval(patches);
    }

    private void validateGivenNameRemoval(JsonPatches patches) {
        Stream.of(
            patches.getFirstForename(),
            patches.getSecondForename(),
            patches.getOtherForenames())
            .flatMap(Optional::stream)
            .filter(amendmentPatch -> amendmentPatch.getOp() == AmendmentPatchOperation.REMOVE)
            .map(AmendmentPatch::getPath)
            .reduce((a, b) -> String.join(", ", a, b))
            .ifPresent(paths -> {
                throw new FhirValidationException(String.format(
                    "Removing %s is illegal. Use %s to remove all forenames instead", paths, JsonPatches.ALL_FORENAMES_PATH));
            });

        var anyNameChange = Stream.of(
            patches.getFirstForename(),
            patches.getSecondForename(),
            patches.getOtherForenames())
            .flatMap(Optional::stream)
            .anyMatch(AmendmentNameToEdifactTranslator::amendmentPatchRequiringValue);

        if (anyNameChange && patches.getAllForenamesPath().isPresent()) {
            throw new FhirValidationException("Illegal to modify forenames and remove all at the same time");
        }
    }

    private void validateFamilyNameRemoval(JsonPatches patches) {
        if (patches.getSurname()
            .filter(amendmentPatch -> amendmentPatch.getOp() == AmendmentPatchOperation.REMOVE)
            .isPresent()) {
            throw new FhirValidationException("Removing surnames is illegal");
        }
    }
}
