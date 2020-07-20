package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatchTransactionMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EdifactToPatchService {

    private final List<PatchTransactionMapper> patchTransactionMappers;

    public AmendmentBody convertToPatch(Transaction transaction) {

        var nhsNumber = getNhsNumber(transaction);
        var gpCode = getGpCode(transaction);
        var gpTradingPartnerCode = getGpTradingPartnerCode(transaction);
        var healthcarePartyCode = getHealthcarePartyCode(transaction);

        var patches = getPatches(transaction);

        return AmendmentBody.builder()
            .nhsNumber(nhsNumber)
            .gpCode(gpCode)
            .gpTradingPartnerCode(gpTradingPartnerCode)
            .healthcarePartyCode(healthcarePartyCode)
            .patches(patches)
            .build();
    }

    private List<AmendmentPatch> getPatches(Transaction transaction) {
        var amendmentPatches = patchTransactionMappers.stream()
            .map(patchTransactionMapper -> patchTransactionMapper.map(transaction))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (amendmentPatches.isEmpty()) {
            throw new EdifactValidationException("No patched has been produces.");
        }

        return amendmentPatches;
    }

    private String getHealthcarePartyCode(Transaction transaction) {
        return transaction.getMessage().getHealthAuthorityNameAndAddress().getIdentifier();
    }

    private String getGpTradingPartnerCode(Transaction transaction) {
        return transaction.getMessage().getInterchange().getInterchangeHeader().getRecipient();
    }

    private String getGpCode(Transaction transaction) {
        return transaction.getGpNameAndAddress().getIdentifier();
    }

    private String getNhsNumber(Transaction transaction) {
        return transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .orElseThrow(() -> new EdifactValidationException("Missing mandatory NHS number"));
    }
}
