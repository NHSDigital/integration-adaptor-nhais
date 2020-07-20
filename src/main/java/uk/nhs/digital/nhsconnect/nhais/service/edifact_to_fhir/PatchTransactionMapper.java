package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchRemoval;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;

public interface PatchTransactionMapper {
    AmendmentPatch map(Transaction transaction);

    String REMOVE_INDICATOR = "%";

    default AmendmentPatch createAmendmentPatch(String element, String path) {
        if (element == null) {
            return null;
        }
        if (element.isBlank()) {
            return new AmendmentPatch(AmendmentPatchOperation.REPLACE, path, null);
        } else if (element.equals(REMOVE_INDICATOR)) {
            return new AmendmentPatchRemoval(AmendmentPatchOperation.REMOVE, path, null);
        }
        return new AmendmentPatch(AmendmentPatchOperation.REPLACE, path, AmendmentValue.from(element));
    }
}
