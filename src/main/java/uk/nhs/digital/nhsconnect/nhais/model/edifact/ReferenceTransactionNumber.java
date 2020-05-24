package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 *class declaration:
 */
@Getter @Setter
public class ReferenceTransactionNumber extends Reference {

    private String qualifier;
    private String reference;

    public ReferenceTransactionNumber(@NonNull String qualifier, @NonNull String reference) {
        super("TN", reference);
        this.qualifier = qualifier;
        this.reference = reference;
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (qualifier.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (qualifier.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }
}
