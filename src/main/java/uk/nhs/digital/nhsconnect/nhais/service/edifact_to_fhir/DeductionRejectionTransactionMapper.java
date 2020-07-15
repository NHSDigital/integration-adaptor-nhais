package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.List;
import java.util.Optional;

@Component
public class DeductionRejectionTransactionMapper implements FhirTransactionMapper {

    @Override
    public void map(Parameters parameters, Transaction transaction) {
        var nhsIdentifier = transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .flatMap(FhirTransactionMapper::mapToNhsIdentifier)
            .orElseThrow(() -> new EdifactValidationException("NHS Number is mandatory for inbound deduction request rejection"));
         ParametersExtension.extractPatient(parameters).setIdentifier(List.of(nhsIdentifier));

        var freeText = transaction
            .getFreeText()
            .orElseThrow(() -> new EdifactValidationException("HA Notes (Free Text) are mandatory for inbound deduction request rejection"));
        parameters.addParameter()
            .setName(ParameterNames.FREE_TEXT)
            .setValue(new StringType(freeText.getTextLiteral()));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.DEDUCTION_REJECTION;
    }
}
