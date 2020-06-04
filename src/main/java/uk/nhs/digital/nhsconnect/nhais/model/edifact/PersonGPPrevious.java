package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class PersonGPPrevious extends Segment {
    private final static String PGP_PREFIX = "PGP";
    private final static String PGP_SUFFIX = ",281:900";

    //NAD+PGP+4826940,281:900'
    private @NonNull String practitioner;

    @Override
    public String getKey() {
        return "NAD";
    }

    @Override
    public String getValue() {
        return PGP_PREFIX
                .concat(PLUS_SEPARATOR)
                .concat(practitioner)
                .concat(PGP_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(practitioner) || practitioner.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
    }
}
