package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.GpTradingPartnerCode;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.PatientParameter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;

public interface FhirTransactionMapper {
    Parameters map(Transaction transaction);

    ReferenceTransactionType.TransactionType getTransactionType();

    static Parameters createParameters(Transaction transaction) {
        return new Parameters()
            .addParameter(new GpTradingPartnerCode(transaction.getMessage().getInterchange()))
            .addParameter(new PatientParameter(transaction));
    }

}
