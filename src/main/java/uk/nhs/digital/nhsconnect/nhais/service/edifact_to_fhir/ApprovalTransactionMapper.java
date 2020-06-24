package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;

@Component
public class ApprovalTransactionMapper implements TransactionMapper {
    @Override
    public void map(Parameters parameters, TransactionV2 transaction) {
        transaction.getPersonName()
            .flatMap(PersonName::getNhsNumber)
            .ifPresent(nhsIdentifier -> ParametersExtension.extractPatient(parameters).setIdentifier(List.of(nhsIdentifier)));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.TransactionType.APPROVAL;
    }
}
