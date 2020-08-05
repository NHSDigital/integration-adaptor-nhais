package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

@Component
public class ApprovalTransactionMapper implements FhirTransactionMapper {
    @Override
    public Parameters map(Transaction transaction) {
        var parameters = FhirTransactionMapper.createParameters(transaction);

        transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .map(NhsIdentifier::new)
            .ifPresent(nhsIdentifier -> ParametersExtension.extractPatient(parameters).setIdentifier(List.of(nhsIdentifier)));
        return parameters;
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.APPROVAL;
    }

}
