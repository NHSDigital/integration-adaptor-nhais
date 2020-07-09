package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;
import java.util.Optional;

@Component
public class ApprovalTransactionMapper implements TransactionMapper {
    @Override
    public void map(Parameters parameters, Transaction transaction) {
        transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .flatMap(this::mapToNhsIdentifier)
            .ifPresent(nhsIdentifier -> ParametersExtension.extractPatient(parameters).setIdentifier(List.of(nhsIdentifier)));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.APPROVAL;
    }

    private Optional<NhsIdentifier> mapToNhsIdentifier(String nhsNumber) {
        return Optional.ofNullable(nhsNumber).map(NhsIdentifier::new);
    }
}
