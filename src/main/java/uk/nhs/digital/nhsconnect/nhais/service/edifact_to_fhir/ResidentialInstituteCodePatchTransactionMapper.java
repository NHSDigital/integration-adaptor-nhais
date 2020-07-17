package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ResidentialInstituteExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentStringExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;

import org.springframework.stereotype.Component;

@Component
public class ResidentialInstituteCodePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var residentialInstitute = transaction.getResidentialInstitution();
        if (residentialInstitute.isPresent()) {
            var residentialInstituteCode = String.valueOf(residentialInstitute.get().getIdentifier());
            return createAmmendmentExtensionPatch(residentialInstituteCode);
        } else {
            return null;
        }
    }

    private AmendmentPatch createAmmendmentExtensionPatch(String element) {
        var path = "/extension/0";

        AmendmentPatch amendmentPatch = new AmendmentPatch();
        if (element == null || element.equals("null")) {
            amendmentPatch.setOp(AmendmentPatchOperation.REMOVE);
            amendmentPatch.setPath(path);
            amendmentPatch.setValue(new AmendmentStringExtension(ResidentialInstituteExtension.URL, "null"));
        } else {
            amendmentPatch.setOp(AmendmentPatchOperation.REPLACE);
            amendmentPatch.setPath(path);
            amendmentPatch.setValue(new AmendmentStringExtension(ResidentialInstituteExtension.URL, element));
        }

        return amendmentPatch;
    }
}
