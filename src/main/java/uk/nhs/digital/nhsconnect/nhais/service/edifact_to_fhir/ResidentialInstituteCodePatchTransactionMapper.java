package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.ResidentialInstituteNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ResidentialInstituteExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentStringExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class ResidentialInstituteCodePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getResidentialInstitution()
            .map(this::createAmendmentExtensionPatch)
            .orElse(null);
    }

    private AmendmentPatch createAmendmentExtensionPatch(ResidentialInstituteNameAndAddress residentialInstitute) {
        var residentialInstituteCode = residentialInstitute.getIdentifier();

        if (residentialInstituteCode == null || residentialInstituteCode.equals("%")) {
            return new AmendmentPatch(AmendmentPatchOperation.REPLACE, JsonPatches.EXTENSION_PATH,
                new AmendmentStringExtension(ResidentialInstituteExtension.URL, null));
        } else {
            return new AmendmentPatch(AmendmentPatchOperation.REPLACE, JsonPatches.EXTENSION_PATH,
                new AmendmentStringExtension(ResidentialInstituteExtension.URL, residentialInstituteCode));
        }
    }
}
