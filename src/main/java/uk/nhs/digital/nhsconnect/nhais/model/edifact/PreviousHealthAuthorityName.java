package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

/**
 * Example NAD+FHS+XX1:954'
 */
@Getter @Setter @RequiredArgsConstructor
public class PreviousHealthAuthorityName extends Segment {

    public static final String KEY = "NAD";
    public static final String QUALIFIER = "PFH";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private @NonNull String identifier;
    private final String code = "954";


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
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (StringUtils.isEmpty(identifier)) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
    }

}
