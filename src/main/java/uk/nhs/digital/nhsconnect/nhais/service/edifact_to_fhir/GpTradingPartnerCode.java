package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import ca.uhn.fhir.model.api.annotation.Block;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.InterchangeV2;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;

@Block()
public class GpTradingPartnerCode extends Parameters.ParametersParameterComponent {
    public GpTradingPartnerCode(InterchangeV2 interchange) {
        super();
        String recipient = interchange.getInterchangeHeader().getRecipient();
        this.setName(ParameterNames.GP_TRADING_PARTNER_CODE);
        this.setValue(new StringType(recipient));
    }

    public GpTradingPartnerCode(String value) {
        super();
        this.setName(ParameterNames.GP_TRADING_PARTNER_CODE);
        this.setValue(new StringType(value));
    }
}
