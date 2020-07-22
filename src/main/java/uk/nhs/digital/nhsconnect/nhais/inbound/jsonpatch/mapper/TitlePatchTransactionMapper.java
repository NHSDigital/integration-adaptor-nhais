package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class TitlePatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonName()
            .map(this::createTitlePatch)
            .orElse(null);
    }

    private AmendmentPatch createTitlePatch(PersonName personName) {
        var title = personName.getTitle();
        return createAmendmentPatch(title, JsonPatches.TITLE_PATH);
    }
}
