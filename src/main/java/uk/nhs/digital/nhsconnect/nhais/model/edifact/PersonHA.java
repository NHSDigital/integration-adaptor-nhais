package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class PersonHA extends Segment {
    private final static String HA_PREFIX = "FHS";
    private final static String HA_SUFFIX = ":954";

    //NAD+FHS+XX1:954'
    private @NonNull String organization;

    @Override
    public String getKey() {
        return "NAD";
    }

    @Override
    public String getValue() {
        return HA_PREFIX
            .concat(PLUS_SEPARATOR)
            .concat(organization)
            .concat(HA_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(organization) || organization.isBlank()) {
            throw new EdifactValidationException(getKey() + ": organization is required");
        }
    }
}
