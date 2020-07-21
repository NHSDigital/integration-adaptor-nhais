package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class ThirdForenamePatchTransactionMapper implements PatchTransactionMapper{

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonName()
            .map(this::createThirdForenamePatch)
            .orElse(null);
    }

    private AmendmentPatch createThirdForenamePatch(PersonName personName) {
        var thirdForename = personName.getOtherForenames();
        return createAmendmentPatch(thirdForename, JsonPatches.OTHER_FORENAMES_PATH);
    }
}
