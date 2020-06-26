package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;

@Component
public class AcceptanceTransactionMapper implements TransactionMapper {
    @Override
    public void map(Parameters parameters, TransactionV2 transaction) {
        //TODO: to be implemented
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.TransactionType.ACCEPTANCE;
    }
}
