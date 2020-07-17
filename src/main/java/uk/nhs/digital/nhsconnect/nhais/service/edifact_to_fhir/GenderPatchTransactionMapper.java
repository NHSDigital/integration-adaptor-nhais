package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class GenderPatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var gender = transaction.getGender();
        if (gender.isPresent()) {
            var genderName = gender.get().getGender().getName().toLowerCase();
            return createAmmendmentPatch(genderName, JsonPatches.SEX_PATH);
        } else {
            return null;
        }
    }
}
