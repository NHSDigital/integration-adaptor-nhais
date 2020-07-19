package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class BirthDatePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        var birthDate = transaction.getBirthDate();
        if (birthDate.isPresent()) {
            var timestamp = birthDate.get().getDateOfBirth();
            return createAmendmentPatch(timestamp.toString(), JsonPatches.BIRTH_DATE_PATH);
        } else {
            return null;
        }
    }
}
