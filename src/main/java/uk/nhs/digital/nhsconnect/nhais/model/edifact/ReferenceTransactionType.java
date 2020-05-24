package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.*;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter @Setter
public class ReferenceTransactionType extends Reference {
    private @NonNull TransactionType transactionType;

    public ReferenceTransactionType(@NonNull TransactionType transactionType) {
        super("950", transactionType.getCode());
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        super.preValidate();
        if(getReference().isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute reference is required");
        }
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        // no stateful properties to validate
    }

    @Getter
    public enum TransactionType {
        ACCEPTANCE("G1", "ACG"),
        AMENDMENT("G2", "AMG"),
        REMOVAL("G3", "REG"),
        DEDUCTION("G4", "DER");

        TransactionType(String code, String abbreviation) {
            this.code = code;
        }

        private String code;
        private String abbreviation;

    }

}
