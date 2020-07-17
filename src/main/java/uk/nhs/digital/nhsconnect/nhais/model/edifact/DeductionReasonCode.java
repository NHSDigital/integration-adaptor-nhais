package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

/**
 * Example GIS+1:ZZZ'
 */
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@AllArgsConstructor
public class DeductionReasonCode extends Segment {

    public final static String KEY = "GIS";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    private final @NonNull String code;

    public static DeductionReasonCode fromString(String edifactString) {
        if (!edifactString.startsWith(DeductionReasonCode.KEY)) {
            throw new IllegalArgumentException("Can't create " + DeductionReasonCode.class.getSimpleName() + " from " + edifactString);
        }
        String[] keySplit = Split.byPlus(edifactString);
        String code = Split.byColon(keySplit[1])[0];
        return new DeductionReasonCode(code);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return code.concat(ZZZ_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (code.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Deduction Reason Code is required");
        }
    }

}
