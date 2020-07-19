package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchRemoval;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

public abstract class AbstractForenameTransactionMapper {

    protected AmendmentPatchRemoval createRemoveForenameAmendmentPatch() {
        AmendmentPatchRemoval amendmentPatchRemoval = new AmendmentPatchRemoval();
        amendmentPatchRemoval.setOp(AmendmentPatchOperation.REMOVE);
        amendmentPatchRemoval.setPath(JsonPatches.ALL_FORENAMES_PATH);
        amendmentPatchRemoval.setValue(null);

        return amendmentPatchRemoval;
    }
}
