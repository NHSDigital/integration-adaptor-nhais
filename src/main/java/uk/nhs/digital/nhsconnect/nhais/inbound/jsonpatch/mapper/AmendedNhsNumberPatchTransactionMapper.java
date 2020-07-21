package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PatientJsonPaths;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.springframework.stereotype.Component;

@Component
public class AmendedNhsNumberPatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getPersonPreviousName()
            .map(this::createNhsNumberPatch)
            .orElse(null);

    }

    private AmendmentPatch createNhsNumberPatch(PersonPreviousName personPreviousName) {
        var amendedNhsNumber = personPreviousName.getNhsNumber();
        return createAmendmentPatch(amendedNhsNumber, PatientJsonPaths.NHS_NUMBER_PATH);
    }
}
