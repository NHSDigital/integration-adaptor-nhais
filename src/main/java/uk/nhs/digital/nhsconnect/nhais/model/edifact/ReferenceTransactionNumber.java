package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceTransactionNumber extends Reference {

    public static final String QUALIFIER = "TN";

    private @NonNull Long transactionNumber;

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (StringUtils.isEmpty(getReference())) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }

    @Override
    protected String getReference() {
        return Long.toString(transactionNumber);
    }

    @Override
    protected String getQualifier() {
        return QUALIFIER;
    }
}
