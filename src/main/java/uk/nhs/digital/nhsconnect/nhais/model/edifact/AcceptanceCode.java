package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.apache.commons.lang3.EnumUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class AcceptanceCode extends Segment {
    /*
        HEA+ACD+A:ZZZ'

        If the transaction is a "G1" one repeat will hold the acceptance type.
        G1 = Acceptance of patient registration details by a GP
        Values = 1 through 5
        If the transaction is a "G1" one repeat will hold the acceptance code.
        "A" = "Acceptance"
        "D" = "Acceptance following a deduction"
        "R" = "Acceptance following a rejection (wrong FHSA)"
        "I" = "Internal transfer within partnership"
        "S" = "Acceptance with same GP new FHSA"
     */
    private final static String KEY = "HEA";
    private final static String ACD_PREFIX = "ACD";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    private @NonNull String code;

    private static boolean isCodeAllowed(String inputCode) {
        return EnumUtils.isValidEnum(AcceptanceCodes.class, inputCode);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return ACD_PREFIX
            .concat(PLUS_SEPARATOR)
            .concat(code)
            .concat(ZZZ_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(code) || code.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Acceptance Code is required");
        }

        if (!isCodeAllowed(code)) {
            throw new EdifactValidationException("Acceptance Code not allowed: " + code);
        }
    }

    private enum AcceptanceCodes {
        A, D, R, I, S
    }
}
