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
public class PersonGPPrevious extends Segment {
    public static final String KEY = "NAD";
    public static final String PREVIOUS_GP_QUALIFIER = "PGP";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + PREVIOUS_GP_QUALIFIER;
    private @NonNull String identifier;
    private @NonNull String code;

    public static PersonGPPrevious fromString(String edifactString) {
        if (!edifactString.startsWith(PersonGPPrevious.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + PersonGPPrevious.class.getSimpleName() + " from " + edifactString);
        }
        String[] keySplit = edifactString.split("\\+");
        String identifier = keySplit[2].split("\\:")[0];
        String code = keySplit[2].split("\\:")[1];
        return new PersonGPPrevious(identifier, code);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return PREVIOUS_GP_QUALIFIER +
            PLUS_SEPARATOR +
            identifier +
            COLON_SEPARATOR +
            code;
    }

    @Override
    protected void validateStateful() {
        //NOP
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (StringUtils.isEmpty(identifier)) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
        if (StringUtils.isEmpty(code)) {
            throw new EdifactValidationException(getKey() + ": Attribute code is required");
        }
    }
}
