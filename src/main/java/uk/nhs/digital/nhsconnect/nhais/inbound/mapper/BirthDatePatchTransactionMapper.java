package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class BirthDatePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonDateOfBirth()
            .map(this::createBirthDatePatch)
            .orElse(null);

    }

    private AmendmentPatch createBirthDatePatch(PersonDateOfBirth personDateOfBirth) {
        var timestamp = personDateOfBirth.getDateOfBirth();
        return createAmendmentPatch(timestamp.toString(), JsonPatches.BIRTH_DATE_PATH);
    }
}
