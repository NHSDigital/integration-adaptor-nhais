package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class PreviousGpName extends Segment {
    public static final String KEY = "NAD";
    public static final String PREVIOUS_GP_QUALIFIER = "PGP";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + PREVIOUS_GP_QUALIFIER;
    private @NonNull String partyName;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return PREVIOUS_GP_QUALIFIER +
            PLUS_SEPARATOR +
            PLUS_SEPARATOR +
            PLUS_SEPARATOR +
            partyName;
    }

    @Override
    protected void validateStateful() {
        //NOP
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (StringUtils.isEmpty(partyName)) {
            throw new EdifactValidationException(getKey() + ": Attribute partyName is required");
        }
    }
}
