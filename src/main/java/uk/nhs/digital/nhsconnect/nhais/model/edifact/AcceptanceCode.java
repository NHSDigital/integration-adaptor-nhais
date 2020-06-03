package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.util.Objects;

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
    private final static String ACD_PREFIX = "ACD";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    private @NonNull String code;

    @Override
    public String getKey() {
        return "HEA";
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
        if (code.isEmpty() || Objects.isNull(code)) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
    }
}
