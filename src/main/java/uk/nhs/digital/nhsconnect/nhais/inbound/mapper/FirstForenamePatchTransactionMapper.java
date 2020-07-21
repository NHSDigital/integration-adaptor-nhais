package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchRemoval;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class FirstForenamePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personName = transaction.getPersonName();
        if (personName.isPresent()) {
            var forename = personName.get().getFirstForename();
            if (forename != null && forename.equals(AmendmentPatch.REMOVE_INDICATOR)) {
                return createRemoveForenameAmendmentPatch();
            }
            return createAmendmentPatch(forename, JsonPatches.FIRST_FORENAME_PATH);
        } else {
            return null;
        }
    }

    private AmendmentPatchRemoval createRemoveForenameAmendmentPatch() {
        AmendmentPatchRemoval amendmentPatchRemoval = new AmendmentPatchRemoval();
        amendmentPatchRemoval.setOp(AmendmentPatchOperation.REMOVE);
        amendmentPatchRemoval.setPath(JsonPatches.ALL_FORENAMES_PATH);
        amendmentPatchRemoval.setValue(null);

        return amendmentPatchRemoval;
    }
}
