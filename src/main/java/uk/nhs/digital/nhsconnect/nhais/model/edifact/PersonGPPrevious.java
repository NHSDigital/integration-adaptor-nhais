package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class PersonGPPrevious extends Segment {
    private final static String KEY = "NAD";
    private final static String PREVIOUS_GP_QUALIFIER = "PGP";
    //TODO LOCAL_GP_CODE is not static value
    private final static String LOCAL_GP_CODE = "281";
    //TODO CODE_LIST_QUALIFIER is not static value
    private final static String CODE_LIST_QUALIFIER = "900";

    private @NonNull String practitioner;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return PREVIOUS_GP_QUALIFIER
            .concat(PLUS_SEPARATOR)
            .concat(practitioner)
            .concat(COMMA_SEPARATOR)
            .concat(LOCAL_GP_CODE)
            .concat(COLON_SEPARATOR)
            .concat(CODE_LIST_QUALIFIER);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(practitioner) || practitioner.isBlank()) {
            throw new EdifactValidationException(getKey() + ": previous practitioner is required");
        }
    }
}
