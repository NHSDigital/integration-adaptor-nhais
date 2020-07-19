package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class ThirdForenamePatchTransactionMapper extends AbstractForenameTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personName = transaction.getPersonName();
        if (personName.isPresent()) {
            var thirdForename = personName.get().getThirdForename();
            if (thirdForename != null && thirdForename.equals(REMOVE_INDICATOR)) {
                return createRemoveForenameAmendmentPatch();
            }
            return createAmendmentPatch(thirdForename, JsonPatches.OTHER_FORENAMES_PATH);
        } else {
            return null;
        }
    }
}