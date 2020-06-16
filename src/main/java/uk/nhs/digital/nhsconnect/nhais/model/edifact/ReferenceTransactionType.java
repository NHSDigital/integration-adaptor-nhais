package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Arrays;

@Getter @Setter
public class ReferenceTransactionType extends Segment {

    public static final String KEY = "RFF";
    public static final String QUALIFIER = "950";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private @NonNull TransactionType transactionType;

    public ReferenceTransactionType(@NonNull TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if(transactionType == null){
            throw new EdifactValidationException(getKey() + ": Attribute transactionType is required");
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER + ":" + transactionType.getCode();
    }

    @Override
    protected void validateStateful() {
        // no stateful properties to validate
    }

    public static ReferenceTransactionType fromString(String edifactString) {
        if(!edifactString.startsWith(ReferenceTransactionType.KEY_QUALIFIER)){
            throw new IllegalArgumentException("Can't create " + ReferenceTransactionType.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = edifactString.split(":");
        return new ReferenceTransactionType(TransactionType.fromCode(split[1]));
    }

    @Getter
    @RequiredArgsConstructor
    public enum TransactionType {
        ACCEPTANCE("G1", "ACG"),
        AMENDMENT("G2", "AMG"),
        REMOVAL("G3", "REG"),
        DEDUCTION("G4", "DER"),
        REJECTION("F3", "REF"),
        APPROVAL("F4", "APF");

        private final String code;
        private final String abbreviation;

        public static TransactionType fromCode(String code){
            return Arrays.stream(TransactionType.values())
                .filter(transactionType1 -> transactionType1.code.equals(code))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        }

    }

}
