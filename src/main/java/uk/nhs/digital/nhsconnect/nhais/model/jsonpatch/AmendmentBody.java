package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class AmendmentBody {

    private String nhsNumber;
    private String gpCode;
    private String gpTradingPartnerCode;
    private String healthcarePartyCode;
    private List<AmendmentPatch> patches;

    @JsonIgnore
    public JsonPatches getJsonPatches() {
        return new JsonPatches(patches);
    }
}
