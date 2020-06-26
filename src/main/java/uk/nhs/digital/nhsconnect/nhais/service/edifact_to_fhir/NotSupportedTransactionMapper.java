package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;

public class NotSupportedTransactionMapper implements TransactionMapper {

    private final ReferenceTransactionType.TransactionType transactionType;

    public NotSupportedTransactionMapper(ReferenceTransactionType.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public void map(Parameters parameters, TransactionV2 transaction) {
        throw new UnsupportedOperationException("Transaction type " + transactionType + " is not supported");
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        throw new UnsupportedOperationException("Transaction type " + transactionType + " is not supported");
    }
}
