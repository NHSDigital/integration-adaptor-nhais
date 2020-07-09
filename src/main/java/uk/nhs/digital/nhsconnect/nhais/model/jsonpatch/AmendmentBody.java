package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.Value;

import java.util.List;

@Value
public class AmendmentBody {

    String nhsNumber;
    String gpCode;
    String gpTradingPartnerCode;
    String healthcarePartyCode;
    List<AmendmentPatch> patches;

    public JsonPatches getJsonPatches() {
        return new JsonPatches(patches);
    }
}

