package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentificationType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentNameToEdifactMapper extends AmendmentToEdifactMapper {
    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var patches = amendmentBody.getJsonPatches();

        var title = patches.getTitle()
            .map(AmendmentPatch::getFormattedSimpleValue)
            .orElse(null);
        var surname = patches.getSurname()
            .map(AmendmentPatch::getFormattedSimpleValue)
            .orElse(null);
        var firstForename = patches.getFirstForename()
            .or(() -> patches
                .getAllForenamesPath()
                .filter(AmendmentPatch::isRemoval))
            .map(AmendmentPatch::getFormattedSimpleValue)
            .orElse(null);
        var secondForename = patches.getSecondForename()
            .map(AmendmentPatch::getFormattedSimpleValue)
            .orElse(null);
        var otherForenames = patches.getOtherForenames()
            .map(AmendmentPatch::getFormattedSimpleValue)
            .orElse(null);

        return PersonName.builder()
            .nhsNumber(amendmentBody.getNhsNumber())
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
            .title(title)
            .surname(surname)
            .firstForename(firstForename)
            .secondForename(secondForename)
            .otherForenames(otherForenames)
            .build();
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return true; // segment should always be created as it contains NHS
    }

    @Override
    void validatePatches(JsonPatches patches) {
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
            .filter(AmendmentPatch::isRemoval)
            .map(AmendmentPatch::getPath)
            .reduce((a, b) -> String.join(", ", a, b))
            .ifPresent(paths -> {
                throw new PatchValidationException(String.format(
                    "Removing %s is illegal. Use %s to remove all forenames instead", paths, JsonPatches.ALL_FORENAMES_PATH));
            });

        var anyNameChange = Stream.of(
            patches.getFirstForename(),
            patches.getSecondForename(),
            patches.getOtherForenames())
            .flatMap(Optional::stream)
            .anyMatch(AmendmentToEdifactMapper::amendmentPatchRequiringValue);

        if (anyNameChange && patches.getAllForenamesPath().isPresent()) {
            throw new PatchValidationException("Illegal to modify forenames and remove all at the same time");
        }
    }

    private void validateFamilyNameRemoval(JsonPatches patches) {
        if (patches.getSurname()
            .filter(AmendmentPatch::isRemoval)
            .isPresent()) {
            throw new PatchValidationException("Removing surnames is illegal");
        }
    }
}
