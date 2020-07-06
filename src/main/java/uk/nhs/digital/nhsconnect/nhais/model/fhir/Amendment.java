package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Amendment {

    private String nhsNumber;
    private String gpCode;
    private String gpTradingPartnerCode;
    private String healthcarePartyCode;
    private List<AmendmentPatch> patches;

}
