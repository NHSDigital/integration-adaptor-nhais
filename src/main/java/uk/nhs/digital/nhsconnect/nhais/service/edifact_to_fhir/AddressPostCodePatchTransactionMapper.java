package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class AddressPostCodePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personAddress = transaction.getPersonAddress();
        if (personAddress.isPresent()) {
            var postalCode = personAddress.get().getPostalCode();
            return createAmendmentPatch(postalCode, JsonPatches.POSTAL_CODE_PATH);
        } else {
            return null;
        }
    }
}
