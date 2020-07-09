package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AmendmentToEdifactMapper {

    protected static boolean amendmentPatchRequiringValue(AmendmentPatch amendmentPatch) {
        return amendmentPatch.getOp() == AmendmentPatchOperation.ADD
            || amendmentPatch.getOp() == AmendmentPatchOperation.REPLACE;
    }

    public Optional<Segment> map(AmendmentBody amendmentBody) throws PatchValidationException {
        var patches = amendmentBody.getJsonPatches();
        if (shouldCreateSegment(amendmentBody)) {
            validatePatches(patches);
            return Optional.of(mapPatches(amendmentBody));
        }
        return Optional.empty();
    }

    abstract void validatePatches(JsonPatches patches) throws PatchValidationException;

    abstract Segment mapPatches(AmendmentBody amendmentBody);

    abstract boolean shouldCreateSegment(AmendmentBody amendmentBody);

    protected void validateNonEmptyValues(List<Optional<AmendmentPatch>> amendmentPatches) {
        var invalidAmendmentPaths = amendmentPatches.stream()
            .flatMap(Optional::stream)
            .filter(AmendmentToEdifactMapper::amendmentPatchRequiringValue)
            .filter(amendmentPatch -> StringUtils.isBlank(amendmentPatch.getValue().get()))
            .map(AmendmentPatch::getPath)
            .collect(Collectors.toList());

        if (!invalidAmendmentPaths.isEmpty()) {
            throw new PatchValidationException("Invalid values for: " + invalidAmendmentPaths);
        }
    }
}
