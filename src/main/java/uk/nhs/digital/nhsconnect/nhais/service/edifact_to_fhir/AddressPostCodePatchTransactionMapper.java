package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class AddressPostCodePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonAddress()
            .map(this::createPostCodePatch)
            .orElse(null);
    }

    private AmendmentPatch createPostCodePatch(PersonAddress personAddress) {
        var postalCode = personAddress.getPostalCode();
        return createAmendmentPatch(postalCode, JsonPatches.POSTAL_CODE_PATH);
    }
}
