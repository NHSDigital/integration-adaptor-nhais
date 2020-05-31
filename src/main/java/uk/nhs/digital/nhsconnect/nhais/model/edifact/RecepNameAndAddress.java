package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 * class declaration:
 */
@Getter
@Setter
@RequiredArgsConstructor
public class RecepNameAndAddress extends Segment {
    private final static String NHS_PREFIX = "FHS:819:201+";
    private final static String NHS_SUFFIX = ":814:202";

    // TODO should be numeric representation
    //example: NHS+FHS:819:201+4826940:814:202'
    private @NonNull String gpCode;

    @Override
    public String getKey() {
        return "NHS";
    }

    @Override
    public String getValue() {
        return NHS_PREFIX
                .concat(gpCode)
                .concat(NHS_SUFFIX);
    }

    @Override
    protected void validateStateful() {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        // Do nothing
    }
}
