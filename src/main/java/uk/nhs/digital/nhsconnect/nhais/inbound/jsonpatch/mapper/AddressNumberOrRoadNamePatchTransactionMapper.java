package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import java.util.Objects;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AddressNumberOrRoadNamePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonAddress()
            .map(this::createNumberOrRoadNamePatch)
            .orElse(null);
    }

    private AmendmentPatch createNumberOrRoadNamePatch(PersonAddress personAddress) {
        var addressLine = personAddress.getAddressLine2();
        return createAmendmentPatch(Objects.requireNonNullElse(addressLine, StringUtils.EMPTY), JsonPatches.NUMBER_OR_ROAD_NAME_PATH);
    }
}
