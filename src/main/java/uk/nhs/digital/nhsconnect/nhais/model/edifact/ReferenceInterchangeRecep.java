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
public class ReferenceInterchangeRecep extends Reference {
    public static final String QUALIFIER = "RIS";
    private static final String OK_PARAM = " OK:";
    private @NonNull Long interchangeNumber;
    private @NonNull int counter;

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (StringUtils.isEmpty(getReference())) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }

    @Override
    protected String getReference() {
        return String.format("%08d", interchangeNumber)
                .concat(OK_PARAM)
                .concat(String.valueOf(counter));
    }

    @Override
    protected String getQualifier() {
        return QUALIFIER;
    }
}
