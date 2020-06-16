package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class PartyQualifier extends Segment {
    private final static String KEY = "NAD";
    private final static String CODE = "FHS";
    private final static String REGISTRATION_ID = ":954";

    //NAD+FHS+XX1:954'
    private @NonNull String organization;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return CODE
            .concat(PLUS_SEPARATOR)
            .concat(organization)
            .concat(REGISTRATION_ID);
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
