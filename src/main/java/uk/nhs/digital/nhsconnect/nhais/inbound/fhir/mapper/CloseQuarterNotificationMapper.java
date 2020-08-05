package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;

@Component
public class CloseQuarterNotificationMapper implements FhirTransactionMapper {

    @Override
    public Parameters map(Transaction transaction) {
        return new Parameters();//this mapper is intentionally left blank, parameters are not modified for close quarter notification
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.CLOSE_QUARTER_NOTIFICATION;
    }
}
