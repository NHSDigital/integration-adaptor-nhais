package uk.nhs.digital.nhsconnect.nhais.inbound.fhir.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

@Component
public class RejectionTransactionMapper implements FhirTransactionMapper {

    @Override
    public Parameters map(Transaction transaction) {
        var parameters = FhirTransactionMapper.createParameters(transaction);
        mapFreeText(parameters, transaction);
        return parameters;
    }

    private void mapFreeText(Parameters parameters, Transaction transaction) {
        var textLiteral = transaction
            .getFreeText()
            .orElseThrow(() -> new EdifactValidationException("FreeText is mandatory for inbound rejection"))
            .getFreeTextValue();

        parameters.addParameter()
            .setName(ParameterNames.FREE_TEXT)
            .setValue(new StringType(textLiteral));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.REJECTION;
    }
}
