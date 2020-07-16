package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

/**
 * Example NAD+NFH+COV:954'
 */
@Getter @Setter @RequiredArgsConstructor
public class NewHealthAuthorityName extends Segment {

    public static final String KEY = "NAD";
    public static final String QUALIFIER = "NFH";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;
    private @NonNull String haName;
    private final String code = "954";

    public static NewHealthAuthorityName fromString(String edifactString) {
        if (!edifactString.startsWith(NewHealthAuthorityName.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + NewHealthAuthorityName.class.getSimpleName() + " from " + edifactString);
        }
        String[] keySplit = Split.byPlus(edifactString);
        String haName = Split.byColon(keySplit[2])[0];
        return new NewHealthAuthorityName(haName);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER + "+" + haName + ":" + code;
    }

    @Override
    protected void validateStateful() {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (StringUtils.isEmpty(haName)) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
    }

}
