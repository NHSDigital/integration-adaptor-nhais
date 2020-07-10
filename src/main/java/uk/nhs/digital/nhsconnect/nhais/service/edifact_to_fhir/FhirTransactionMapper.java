package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;

public interface FhirTransactionMapper {
    void map(Parameters parameters, Transaction transaction);

    ReferenceTransactionType.TransactionType getTransactionType();
}
