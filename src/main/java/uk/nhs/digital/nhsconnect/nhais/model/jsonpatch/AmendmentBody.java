package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AmendmentBody {

    private String nhsNumber;
    private String gpCode;
    private String gpTradingPartnerCode;
    private String healthcarePartyCode;
    private List<AmendmentPatch> patches;

    public JsonPatches getJsonPatches() {
        return new JsonPatches(this, patches);
    }
}

