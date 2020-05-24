package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *class declaration:
 */
@Getter @Setter @RequiredArgsConstructor
public class NameAndAddress extends Segment{

    private @NonNull String qualifier;
    private @NonNull String identifier;
    private @NonNull String code;

    @Override
    public String getKey() {
        return "NAD";
    }

    @Override
    public String getValue() {
        // identifier = "HA456";
        code = "956";
        qualifier = "FHS";
        return qualifier + "+" + identifier + "+" + code;
    }

    @Override
    protected void validateStateful() {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (identifier.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
        if (code.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute code is required");
        }
        if(qualifier.isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }
}
