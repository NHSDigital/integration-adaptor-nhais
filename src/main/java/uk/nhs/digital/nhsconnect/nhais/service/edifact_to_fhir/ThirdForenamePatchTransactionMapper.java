package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchRemoval;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class ThirdForenamePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personName = transaction.getPersonName();
        if (personName.isPresent()) {
            var thirdForename = personName.get().getOtherForenames();
            if (thirdForename != null && thirdForename.equals(REMOVE_INDICATOR)) {
                return new AmendmentPatchRemoval(JsonPatches.ALL_FORENAMES_PATH);
            }
            return createAmendmentPatch(thirdForename, JsonPatches.OTHER_FORENAMES_PATH);
        } else {
            return null;
        }
    }
}
