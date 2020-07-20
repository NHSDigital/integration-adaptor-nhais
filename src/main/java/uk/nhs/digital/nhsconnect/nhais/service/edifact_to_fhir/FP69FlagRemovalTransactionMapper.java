package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;

@Component
public class FP69FlagRemovalTransactionMapper implements FhirTransactionMapper {

    @Override
    public void map(Parameters parameters, Transaction transaction) {
        var nhsIdentifier = transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .map(NhsIdentifier::new)
            .orElseThrow(() -> new EdifactValidationException("NHS Number is mandatory for inbound FP69 flag removal transaction"));
        ParametersExtension.extractPatient(parameters).setIdentifier(List.of(nhsIdentifier));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.FP69_FLAG_REMOVAL;
    }
}
