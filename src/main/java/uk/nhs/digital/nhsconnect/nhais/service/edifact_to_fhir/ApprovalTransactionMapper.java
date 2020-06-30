package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

@Component
public class ApprovalTransactionMapper implements TransactionMapper {
    @Override
    public void map(Parameters parameters, Transaction transaction) {
        transaction.getPersonName()
            .flatMap(PersonName::getNhsNumber)
            .ifPresent(nhsIdentifier -> ParametersExtension.extractPatient(parameters).setIdentifier(List.of(nhsIdentifier)));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.TransactionType.IN_APPROVAL;
    }
}
