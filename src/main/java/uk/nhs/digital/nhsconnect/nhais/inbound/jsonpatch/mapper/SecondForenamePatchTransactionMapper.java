package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class SecondForenamePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonName()
            .map(this::createSecondForenamePatch)
            .orElse(null);
    }

    private AmendmentPatch createSecondForenamePatch(PersonName personName) {
        var middleName = personName.getSecondForename();
        return createAmendmentPatch(middleName, JsonPatches.SECOND_FORENAME_PATH);
    }
}
