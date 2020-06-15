package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

@Component
public class RejectionTransactionMapper implements TransactionMapper {

    private static final String FREE_TEXT = "freeText";
    private static final String GP_TRADING_PARTNER_CODE = "gpTradingPartnerCode";

    @Override
    public void map(Parameters parameters, Interchange interchange) {
        mapFreeText(parameters, interchange);
        mapGpTradingPartnerCode(parameters, interchange);
    }

    private void mapFreeText(Parameters parameters, Interchange interchange) {
        //TODO: interchange data from new segment
        parameters.addParameter()
            .setName(FREE_TEXT)
            .setValue(new StringType("WRONG HA - TRY SURREY"));
    }

    private void mapGpTradingPartnerCode(Parameters parameters, Interchange interchange) {
        parameters.addParameter()
            .setName(GP_TRADING_PARTNER_CODE)
            .setValue(new StringType(interchange.getInterchangeHeader().getRecipient()));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.TransactionType.REJECTION;
    }
}
