package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchRemoval;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;

public interface PatchTransactionMapper {
    AmendmentPatch map(Transaction transaction);

    default AmendmentPatch createAmendmentPatch(String value, String path) {
        if (value == null) {
            return null;
        }
        if (value.isBlank()) {
            return new AmendmentPatch(AmendmentPatchOperation.REPLACE, path, null);
        } else if (value.equals(AmendmentPatch.REMOVE_INDICATOR)) {
            return new AmendmentPatchRemoval(path);
        }
        return new AmendmentPatch(AmendmentPatchOperation.REPLACE, path, AmendmentValue.from(value));
    }
}
