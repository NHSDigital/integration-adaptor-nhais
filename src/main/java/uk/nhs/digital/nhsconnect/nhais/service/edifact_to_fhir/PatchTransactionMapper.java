package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;

public interface PatchTransactionMapper {
    AmendmentPatch map(Transaction transaction);

    String REMOVE_INDICATOR = "%";

    default AmendmentPatch createAmmendmentPatch(String element, String path) {
        AmendmentPatch amendmentPatch = new AmendmentPatch();
        if (element == null) {
            return null;
        }
        if (element.isBlank()) {
            amendmentPatch.setOp(AmendmentPatchOperation.REPLACE);
            amendmentPatch.setPath(path);
            amendmentPatch.setValue(null);
        } else if (element.equals(REMOVE_INDICATOR)) {
            amendmentPatch.setOp(AmendmentPatchOperation.REMOVE);
            amendmentPatch.setPath(path);
        } else {
            amendmentPatch.setOp(AmendmentPatchOperation.REPLACE);
            amendmentPatch.setPath(path);
            amendmentPatch.setValue(AmendmentValue.from(element));
        }

        return amendmentPatch;
    }
}
