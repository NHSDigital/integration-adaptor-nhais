package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import java.util.Objects;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AddressLocalityPatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return  transaction.getPersonAddress()
            .map(this::createLocalityPatch)
            .orElse(null);
    }

    private AmendmentPatch createLocalityPatch(PersonAddress personAddress) {
        var addressLine = personAddress.getAddressLine3();
        return createAmendmentPatch(Objects.requireNonNullElse(addressLine, StringUtils.EMPTY), JsonPatches.LOCALITY);
    }
}
