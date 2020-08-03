package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper.PatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.stereotype.Component;

@Component
public class GenderPatchTransactionMapper implements PatchTransactionMapper {

    @Override
    public AmendmentPatch map(Transaction transaction) {
        return transaction.getGender()
            .map(this::createGenderPatch)
            .orElse(null);
    }

    private AmendmentPatch createGenderPatch(PersonSex personSex) {
        var genderName = PersonSex.Gender.toFhir(personSex.getGender());
        return createAmendmentPatch(genderName.toCode(), JsonPatches.SEX_PATH);
    }
}
