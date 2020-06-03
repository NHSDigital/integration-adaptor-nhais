package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import org.springframework.util.StringUtils;

@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor
public class ReferenceTransactionNumber extends Segment {

    public static final String KEY = "RFF";
    public static final String QUALIFIER = "TN";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
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
        if (StringUtils.isEmpty(transactionNumber)) {
            throw new EdifactValidationException(getKey() + ": Attribute reference is required");
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
