package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

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
        if (!edifactString.startsWith(ReferenceTransactionType.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + ReferenceTransactionType.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = Split.byColon(edifactString);
        return new ReferenceTransactionType(TransactionType.fromCode(split[1]));
    }

    @Getter
    @RequiredArgsConstructor
    public enum TransactionType {
        OUT_ACCEPTANCE("G1", "ACG"),
        OUT_AMENDMENT("G2", "AMG"),
        OUT_REMOVAL("G3", "REG"),
        OUT_DEDUCTION("G4", "DER"),
        IN_AMENDMENT("F1", "AMF"),
        IN_DEDUCTION("F2", "DEF"),
        IN_REJECTION("F3", "REF"),
        IN_APPROVAL("F4", "APF");

        private final String code;
        private final String abbreviation;

        public static TransactionType fromCode(String code){
            return Arrays.stream(TransactionType.values())
                .filter(transactionType -> transactionType.code.equals(code))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        }

        @Override
        public String toString() {
            return name().toLowerCase().split("_")[1];
        }
    }
}
