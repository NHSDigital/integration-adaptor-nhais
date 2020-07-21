package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBooleanExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;

import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

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
        return createAmendmentExtensionPatch(drugMarketBoolean);
    }

    private AmendmentPatch createAmendmentExtensionPatch(boolean element) {
        if (element) {
            return new AmendmentPatch(AmendmentPatchOperation.REPLACE, JsonPatches.EXTENSION_PATH,
                new AmendmentBooleanExtension(DrugsMarkerExtension.URL, true));
        }
        return new AmendmentPatch(AmendmentPatchOperation.REPLACE, JsonPatches.EXTENSION_PATH,
                new AmendmentBooleanExtension(DrugsMarkerExtension.URL, false));
    }
}
