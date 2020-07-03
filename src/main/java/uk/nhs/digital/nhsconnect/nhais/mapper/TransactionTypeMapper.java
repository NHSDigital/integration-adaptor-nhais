package uk.nhs.digital.nhsconnect.nhais.mapper;

import uk.nhs.digital.nhsconnect.nhais.exceptions.ParameterValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import org.springframework.stereotype.Component;

@Component
public class TransactionTypeMapper {

    public ReferenceTransactionType.Outbound mapTransactionType(String transactionTypeParam) {
        switch(transactionTypeParam) {
            case "$nhais.acceptance": return ReferenceTransactionType.Outbound.ACCEPTANCE;
            case "$nhais.removal": return ReferenceTransactionType.Outbound.REMOVAL;
            case "$nhais.deduction": return ReferenceTransactionType.Outbound.DEDUCTION;
            default: throw new ParameterValidationException("Unknown Patient operation " + transactionTypeParam);
        }
    }

}
