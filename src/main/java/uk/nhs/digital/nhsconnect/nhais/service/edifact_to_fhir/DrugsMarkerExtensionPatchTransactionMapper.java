package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBooleanExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;

@Component
public class DrugsMarkerExtensionPatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var drugMarker = transaction.getDrugsMarker();
        if (drugMarker.isPresent()) {
            var drugMarketBoolean = drugMarker.get().getDrugsMarker();
            return createAmmendmentExtensionPatch(drugMarketBoolean);
        } else {
            return null;
        }
    }

    private AmendmentPatch createAmmendmentExtensionPatch(boolean element) {
        var path = "/extension/0";

        AmendmentPatch amendmentPatch = new AmendmentPatch();
        if (!element){
            amendmentPatch.setOp(AmendmentPatchOperation.REMOVE);
            amendmentPatch.setPath(path);
            amendmentPatch.setValue(new AmendmentBooleanExtension(DrugsMarkerExtension.URL, "false"));
        } else {
            amendmentPatch.setOp(AmendmentPatchOperation.REPLACE);
            amendmentPatch.setPath(path);
            amendmentPatch.setValue(new AmendmentBooleanExtension(DrugsMarkerExtension.URL, "true"));
        }

        return amendmentPatch;
    }
}
