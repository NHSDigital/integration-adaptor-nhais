package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

/**
 * class declaration:
 */
@Getter
@Setter
@RequiredArgsConstructor
public class RecepNationalHealthBody extends Segment {
    private final static String KEY = "NHS";

    //example: NHS+FHS:819:201+4826940:814:202'
    private @NonNull String cipher;
    private @NonNull String gpCode;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return cipher.concat(":819:201+")
                .concat(gpCode)
                .concat(":814:202");
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
