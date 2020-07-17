package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class PreviousNamePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var previousName = transaction.getPersonPreviousName();
        if (previousName.isPresent()) {
            var amendedNhsNumber = previousName.get().getNhsNumber();
            return createAmmendmentPatch(amendedNhsNumber, JsonPatches.NHS_NUMBER_PATH);
        } else {
            return null;
        }
    }
}
