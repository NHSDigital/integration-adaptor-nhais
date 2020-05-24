package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 *class declaration:
 */
@Getter @Setter @RequiredArgsConstructor
public abstract class Reference extends Segment{

    private @NonNull String qualifier;
    private @NonNull String reference;

    @Override
    public String getKey() {
        return "RFF";
    }

    @Override
    public String getValue() {
        return qualifier + ":" + reference;
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (qualifier.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
        if(reference.isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute reference is required");
        }
    }
}
