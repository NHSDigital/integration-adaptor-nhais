package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = false)
public class ResidentialInstituteNameAndAddress extends Segment {

    public static final String KEY = "NAD";
    public static final String QUALIFIER = "RIC";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private final String code = "956";
    private @NonNull String identifier;

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

    public static ResidentialInstituteNameAndAddress fromString(String edifactString) {
        if (!edifactString.startsWith(ResidentialInstituteNameAndAddress.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + ResidentialInstituteNameAndAddress.class.getSimpleName() + " from " + edifactString);
        }
        String[] components = Split.byPlus(edifactString);
        String code = Split.byColon(components[2])[0];
        return ResidentialInstituteNameAndAddress.builder().identifier(code).build();
    }
}
