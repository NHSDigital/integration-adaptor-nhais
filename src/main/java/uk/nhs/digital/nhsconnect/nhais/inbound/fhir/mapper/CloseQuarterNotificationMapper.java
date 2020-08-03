package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;

import org.hl7.fhir.r4.model.Parameters;

public class CloseQuarterNotificationMapper implements FhirTransactionMapper{

    @Override
    public void map(Parameters parameters, Transaction transaction) {
        //this mapper is intentionally left blank, parameters are not modified for close quarter notification
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.CLOSE_QUARTER_NOTIFICATION;
    }
}
