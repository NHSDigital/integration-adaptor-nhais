package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class ForenamePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personName = transaction.getPersonName();
        if (personName.isPresent()) {
            var forename = personName.get().getForename();
            return createAmendmentPatch(forename, JsonPatches.FIRST_FORENAME_PATH);
        } else {
            return null;
        }
    }
}
