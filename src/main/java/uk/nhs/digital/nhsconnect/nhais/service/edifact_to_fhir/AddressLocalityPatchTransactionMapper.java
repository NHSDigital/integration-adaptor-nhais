package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import java.util.Objects;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AddressLocalityPatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personAddress = transaction.getPersonAddress();
        if (personAddress.isPresent()) {
            var addressLine = personAddress.get().getAddressLine3();
            return createAmendmentPatch(Objects.requireNonNullElse(addressLine, StringUtils.EMPTY), JsonPatches.LOCALITY);
        } else {
            return null;
        }
    }
}
