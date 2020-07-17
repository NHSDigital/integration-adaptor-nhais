package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class AddressHouseNamePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personAddress = transaction.getPersonAddress();
        if (personAddress.isPresent()) {
            var addressLine = personAddress.get().getAddressLine1();
            return createAmendmentPatch(addressLine, JsonPatches.HOUSE_NAME_PATH);
        } else {
            return null;
        }
    }
}
