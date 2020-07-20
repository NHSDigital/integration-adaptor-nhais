package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchRemoval;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class SecondForenamePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personName = transaction.getPersonName();
        if (personName.isPresent()) {
            var middleName = personName.get().getSecondForename();
            if (middleName != null && middleName.equals(REMOVE_INDICATOR)) {
                return new AmendmentPatchRemoval(JsonPatches.ALL_FORENAMES_PATH);
            }
            return createAmendmentPatch(middleName, JsonPatches.SECOND_FORENAME_PATH);
        } else {
            return null;
        }
    }
}
