package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceTransactionType extends Reference {
    public static final String QUALIFIER = "950";

    private @NonNull TransactionType transactionType;

    public static ReferenceTransactionTypeBuilder builder() {
        return new ReferenceTransactionTypeBuilder();
    }

    @Override
    protected String getReference() {
        return transactionType.getCode();
    }

    @Override
    protected String getQualifier() {
        return QUALIFIER;
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
