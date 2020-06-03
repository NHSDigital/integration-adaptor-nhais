package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor
public class ReferenceTransactionNumber extends Segment {

    public static final String KEY = "RFF";
    public static final String QUALIFIER = "TN";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private static final long MAX_TRANSACTION_NUMBER = 9_999_999L;

    private @NonNull Long transactionNumber;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER + ":" + transactionNumber;
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        //NOP
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (transactionNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute reference is required");
        }
        if (transactionNumber < 1 || transactionNumber > MAX_TRANSACTION_NUMBER) {
            throw new EdifactValidationException(
                getKey() + ": Attribute transactionNumber must be between 1 and " + MAX_TRANSACTION_NUMBER);
        }
    }

    public static ReferenceTransactionNumber fromString(String edifactString) {
        if(!edifactString.startsWith(ReferenceTransactionNumber.KEY_QUALIFIER)){
            throw new IllegalArgumentException("Can't create " + ReferenceTransactionNumber.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = edifactString.split("\\:");
        return new ReferenceTransactionNumber(Long.valueOf(split[1]));
    }
}
