package uk.nhs.digital.nhsconnect.nhais.mapper;

import uk.nhs.digital.nhsconnect.nhais.exceptions.ParameterValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import org.springframework.stereotype.Component;

@Component
public class TransationTypeMapper {

    public ReferenceTransactionType.TransactionType mapTransactionType(String transactionTypeParam) {
        switch(transactionTypeParam) {
            case "$nhais.acceptance": return ReferenceTransactionType.TransactionType.ACCEPTANCE;
            case "$nhais.removal": return ReferenceTransactionType.TransactionType.REMOVAL;
            case "$nhais.deduction": return ReferenceTransactionType.TransactionType.DEDUCTION;
            default: throw new ParameterValidationException("Provided transaction type is not allowed");
        }
    }

}
