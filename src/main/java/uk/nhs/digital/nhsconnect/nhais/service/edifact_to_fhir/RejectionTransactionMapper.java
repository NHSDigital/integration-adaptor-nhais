package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

@Component
public class RejectionTransactionMapper implements TransactionMapper {

    @Override
    public void map(Parameters parameters, TransactionV2 transaction) {
        mapFreeText(parameters, transaction);
    }

    private void mapFreeText(Parameters parameters, TransactionV2 transaction) {
        parameters.addParameter()
            .setName(ParameterNames.FREE_TEXT)
            .setValue(new StringType(transaction.getFreeText().getTextLiteral()));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.TransactionType.REJECTION;
    }
}
