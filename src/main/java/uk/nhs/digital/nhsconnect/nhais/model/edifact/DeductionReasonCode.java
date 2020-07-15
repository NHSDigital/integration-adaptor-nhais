package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Builder
@RequiredArgsConstructor
public class DeductionReasonCode extends Segment {
    /*
        GIS+1:ZZZ'
     */
    private final static String KEY = "GIS";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    private final @NonNull String code;

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
        if (Objects.isNull(code) || code.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Deduction Reason Code is required");
        }
    }

}
