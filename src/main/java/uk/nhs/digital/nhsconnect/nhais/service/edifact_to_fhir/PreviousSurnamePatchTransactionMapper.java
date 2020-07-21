package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class PreviousSurnamePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonPreviousName()
            .map(this::createPreviousSurnamePatch)
            .orElse(null);
    }

    private AmendmentPatch createPreviousSurnamePatch(PersonPreviousName personPreviousName) {
        var previousSurname = personPreviousName.getFamilyName();
        return createAmendmentPatch(previousSurname, JsonPatches.PREVIOUS_SURNAME_PATH);
    }
}
