package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.util.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

/**
 * Example NAD+GP+2750922,295:900'
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class GpNameAndAddress extends Segment {

    public static final String KEY = "NAD";
    public static final String QUALIFIER = "GP";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private @NonNull String identifier;
    private @NonNull String code;

    public static GpNameAndAddress fromString(String edifactString) {
        if (!edifactString.startsWith(GpNameAndAddress.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + GpNameAndAddress.class.getSimpleName() + " from " + edifactString);
        }
        String[] keySplit = Split.byPlus(edifactString);
        String identifier = Split.byColon(keySplit[2])[0];
        String code = Split.byColon(keySplit[2])[1];
        return new GpNameAndAddress(identifier, code);
    }

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
        if (StringUtils.isEmpty(code)) {
            throw new EdifactValidationException(getKey() + ": Attribute code is required");
        }
    }
}
