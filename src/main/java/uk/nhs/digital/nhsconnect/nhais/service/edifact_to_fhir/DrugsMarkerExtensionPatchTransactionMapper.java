package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBooleanExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;

import org.springframework.stereotype.Component;

@Component
public class DrugsMarkerExtensionPatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getDrugsMarker()
            .map(this::createDrugsMarkerPatch)
            .orElse(null);
    }

    private AmendmentPatch createDrugsMarkerPatch(DrugsMarker drugMarker) {
        var drugMarketBoolean = drugMarker.isDrugsMarker();
        return createAmmendmentExtensionPatch(drugMarketBoolean);
    }

    private AmendmentPatch createAmmendmentExtensionPatch(boolean element) {
        var path = "/extension/0";

        if (element) {
            return new AmendmentPatch(AmendmentPatchOperation.REPLACE, path,
                new AmendmentBooleanExtension(DrugsMarkerExtension.URL, "true"));
        }
        return new AmendmentPatch(AmendmentPatchOperation.REPLACE, path,
                new AmendmentBooleanExtension(DrugsMarkerExtension.URL, "false"));
    }
}
