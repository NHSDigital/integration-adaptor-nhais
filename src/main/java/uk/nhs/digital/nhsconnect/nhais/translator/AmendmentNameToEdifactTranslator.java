package uk.nhs.digital.nhsconnect.nhais.translator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentNameToEdifactTranslator implements AmendmentToEdifactTranslator {
    @Override
    public List<Segment> translate(AmendmentBody amendmentBody) throws FhirValidationException {
        var patches = amendmentBody.getJsonPatches();
        validatePatches(patches);
        return mapAllPatches(patches);
    }

    private List<Segment> mapAllPatches(JsonPatches patches) {
        return Stream.<Function<JsonPatches, Optional<? extends Segment>>>of(
            this::mapPersonName,
            this::mapPreviousName)
            .map(x -> x.apply(patches))
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }

    private Optional<PersonName> mapPersonName(JsonPatches patches) {
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
            return Optional.of(personName);
        }
        return Optional.empty();
    }

    private Optional<PersonPreviousName> mapPreviousName(JsonPatches patches) {
        return Optional.empty();
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

    private String getValue(AmendmentPatch patch) {
        if (patch.getOp() == AmendmentPatchOperation.REMOVE) {
            return "%";
        }
        return patch.getValue().get();
    }

    private void validatePatches(JsonPatches patches) {
        validateNonEmptyValues(patches);
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
            .reduce((a ,b) -> String.join(", ",  a, b))
            .ifPresent(paths -> {
                throw new EdifactValidationException(String.format(
                    "Removing %s is illegal. Use %s to remove all forenames instead", paths, JsonPatches.ALL_FORENAMES_PATH));
            });

        var anyNameChange = Stream.of(
            patches.getFirstForename(),
            patches.getSecondForename(),
            patches.getOtherForenames())
            .flatMap(Optional::stream)
            .anyMatch(AmendmentNameToEdifactTranslator::amendmentPatchRequiringValue);

        if (anyNameChange && patches.getAllForenamesPath().isPresent()) {
            throw new EdifactValidationException("Illegal to modify forenames and remove all at the same time");
        }
    }

    private void validateFamilyNameRemoval(JsonPatches patches) {
        if (patches.getSurname()
            .filter(amendmentPatch -> amendmentPatch.getOp() == AmendmentPatchOperation.REMOVE)
            .isPresent()) {
            throw new EdifactValidationException("Removing surnames is illegal");
        }
    }

    private void validateNonEmptyValues(JsonPatches patches) {
        var invalidAmendmentPatches = new ArrayList<AmendmentPatch>();
        Stream.of(
            patches.getTitle(),
            patches.getSurname(),
            patches.getPreviousSurname(),
            patches.getFirstForename(),
            patches.getSecondForename(),
            patches.getOtherForenames())
            .flatMap(Optional::stream)
            .filter(AmendmentNameToEdifactTranslator::amendmentPatchRequiringValue)
            .forEach(amendmentPatch -> {
                if (StringUtils.isBlank(amendmentPatch.getValue().get())) {
                    invalidAmendmentPatches.add(amendmentPatch);
                }
            });

        if (!invalidAmendmentPatches.isEmpty()) {
            var pathsWithInvalidValues = invalidAmendmentPatches.stream()
                .map(AmendmentPatch::getPath)
                .collect(Collectors.toList());
            throw new EdifactValidationException("Invalid values for: " + pathsWithInvalidValues);
        }
    }

    private static boolean amendmentPatchRequiringValue(AmendmentPatch amendmentPatch) {
        return amendmentPatch.getOp() == AmendmentPatchOperation.ADD
            || amendmentPatch.getOp() == AmendmentPatchOperation.REPLACE;
    }
}
