package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class AcceptanceType extends Segment {
    private final static String APT_PREFIX = "ATP";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    private @NonNull String type;

    @Override
    public String getKey() {
        return "HEA";
    }

    @Override
    public String getValue() {
        return APT_PREFIX
                .concat(PLUS_SEPARATOR)
                .concat(type)
                .concat(ZZZ_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(type) || type.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
    }
}
