package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class PersonGP extends Segment {
    private final static String GP_PREFIX = "GP";
    private final static String GP_SUFFIX = ",281:900";

    //Registered with GP 281 (GMC National GP Code 4826940).
    //NAD+GP+4826940,281:900'
    private @NonNull String practitioner;

    @Override
    public String getKey() {
        return "NAD";
    }

    @Override
    public String getValue() {
        return GP_PREFIX
                .concat(PLUS_SEPARATOR)
                .concat(practitioner)
                .concat(GP_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(practitioner) || practitioner.isBlank()) {
            throw new EdifactValidationException(getKey() + ": practitioner is required");
        }
    }
}
