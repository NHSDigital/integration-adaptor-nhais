package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.util.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

/**
 * Example NAD+GP+2750922,295:900'
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResidentialInstituteNameAndAddress extends Segment {

    public static final String KEY = "NAD";
    public static final String QUALIFIER = "RIC";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private @NonNull String identifier;
    private final String code = "956";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER + "+" + identifier + ":" + code;
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
    }
}
