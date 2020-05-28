package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

public class ReferenceTransactionNumber extends Reference {

    public ReferenceTransactionNumber() {
        super("TN", "");
    }

    public Long getTransactionNumber() {
        return Long.decode(getReference());
    }

    public ReferenceTransactionNumber setTransactionNumber(Long transactionNumber) {
        this.setReference(Long.toString(transactionNumber));
        return this;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (StringUtils.isEmpty(getReference())) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }
}
