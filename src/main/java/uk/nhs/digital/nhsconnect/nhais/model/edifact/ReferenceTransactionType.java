package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@Setter
public class ReferenceTransactionType extends Segment {

    public static final String KEY = "RFF";
    public static final String QUALIFIER = "950";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private @NonNull TransactionType transactionType;

    public ReferenceTransactionType(@NonNull TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public static ReferenceTransactionType fromString(String edifactString) {
        if (!edifactString.startsWith(ReferenceTransactionType.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + ReferenceTransactionType.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = Split.byColon(edifactString);
        return new ReferenceTransactionType(TransactionType.fromCode(split[1]));
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (transactionType == null) {
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

    @Getter
    @RequiredArgsConstructor
    public enum Inbound implements TransactionType {
        AMENDMENT("F1", "AMF"),
        DEDUCTION("F2", "DEF"),
        REJECTION("F3", "REF"),
        APPROVAL("F4", "APF");

        private final String code;
        private final String abbreviation;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Outbound implements TransactionType {
        ACCEPTANCE("G1", "ACG"),
        AMENDMENT("G2", "AMG"),
        REMOVAL("G3", "REG"),
        DEDUCTION("G4", "DER");

        private final String code;
        private final String abbreviation;
    }

    public interface TransactionType {
        static TransactionType fromCode(String code) {
            return Stream.of(
                Arrays.stream(Inbound.values()),
                Arrays.stream(Outbound.values()))
                .flatMap(Function.identity())
                .map(TransactionType.class::cast)
                .filter(transactionType -> transactionType.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        }

        static TransactionType fromAbbreviation(String abbreviation) {
            return Stream.of(
                Arrays.stream(Inbound.values()),
                Arrays.stream(Outbound.values()))
                .flatMap(Function.identity())
                .map(TransactionType.class::cast)
                .filter(transactionType -> transactionType.getAbbreviation().equals(abbreviation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        }

        String getCode();

        String getAbbreviation();

        default String name() {
            return ((Enum) this).name();
        }
    }
}
