package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AmendmentToEdifactMapper {

    private static final String REMOVE_INDICATOR = "%";

    protected static boolean amendmentPatchRequiringValue(AmendmentPatch amendmentPatch) {
        return amendmentPatch.getOp() == AmendmentPatchOperation.ADD
            || amendmentPatch.getOp() == AmendmentPatchOperation.REPLACE;
    }

    public Optional<Segment> map(AmendmentBody amendmentBody) throws FhirValidationException {
        var patches = amendmentBody.getJsonPatches();
        validatePatches(patches);
        return mapPatches(patches);
    }

    protected void validatePatches(JsonPatches patches) throws FhirValidationException {
    }

    protected abstract Optional<Segment> mapPatches(JsonPatches patches);

    protected String getValue(AmendmentPatch patch) {
        if (patch.getOp() == AmendmentPatchOperation.REMOVE) {
            return REMOVE_INDICATOR;
        }
        return patch.getValue().get();
    }

    protected void validateNonEmptyValues(List<Optional<AmendmentPatch>> amendmentPatches) {
        var invalidAmendmentPatches = new ArrayList<AmendmentPatch>();
        amendmentPatches.stream()
            .flatMap(Optional::stream)
            .filter(AmendmentToEdifactMapper::amendmentPatchRequiringValue)
            .forEach(amendmentPatch -> {
                if (StringUtils.isBlank(amendmentPatch.getValue().get())) {
                    invalidAmendmentPatches.add(amendmentPatch);
                }
            });

        if (!invalidAmendmentPatches.isEmpty()) {
            var pathsWithInvalidValues = invalidAmendmentPatches.stream()
                .map(AmendmentPatch::getPath)
                .collect(Collectors.toList());
            throw new FhirValidationException("Invalid values for: " + pathsWithInvalidValues);
        }
    }
}
