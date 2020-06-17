package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public enum ParameterNames {
    GP_TRADING_PARTNER_CODE("gpTradingPartnerCode"),
    PATIENT("patient");

    private final String name;

}
