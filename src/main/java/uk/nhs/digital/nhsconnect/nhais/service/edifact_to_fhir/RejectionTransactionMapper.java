package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

@Component
public class RejectionTransactionMapper implements TransactionMapper {

    @Override
    public void map(Parameters parameters, Interchange interchange) {
        mapFreeText(parameters, interchange);
    }

    private void mapFreeText(Parameters parameters, Interchange interchange) {
        parameters.addParameter()
            .setName(ParameterNames.FREE_TEXT)
            .setValue(new StringType(interchange.getFreeText().getTextLiteral()));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.TransactionType.REJECTION;
    }
}
