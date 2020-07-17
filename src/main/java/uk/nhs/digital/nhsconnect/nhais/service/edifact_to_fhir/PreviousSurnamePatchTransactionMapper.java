package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class PreviousSurnamePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var personPreviousName = transaction.getPersonPreviousName();
        if (personPreviousName.isPresent()) {
            var previousSurname = personPreviousName.get().getFamilyName();
            return createAmendmentPatch(previousSurname, JsonPatches.PREVIOUS_SURNAME_PATH);
        } else {
            return null;
        }
    }
}
