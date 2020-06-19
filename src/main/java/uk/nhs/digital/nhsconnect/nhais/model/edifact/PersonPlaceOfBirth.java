package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import org.springframework.util.StringUtils;

@Builder
@AllArgsConstructor
public class PersonPlaceOfBirth extends Segment {
    public static final String KEY = "LOC";
    public static final String QUALIFIER = "950";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
    private final String location;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER +
            PLUS_SEPARATOR +
            location;
    }

    @Override
    protected void validateStateful() {
        //NOP
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (StringUtils.isEmpty(location)) {
            throw new EdifactValidationException(getKey() + ": Attribute location is required");
        }
    }
}
