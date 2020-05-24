package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 *class declaration:
 */
@Getter @Setter
public class ReferenceTransactionNumber extends Reference {

    private String reference;

    public ReferenceTransactionNumber(@NonNull Integer reference) {
        super("TN", reference.toString());
        this.reference = reference.toString();
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        // Qualifier is hardcoded, no validation needed?
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (reference == null) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
        if (reference.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }
}
