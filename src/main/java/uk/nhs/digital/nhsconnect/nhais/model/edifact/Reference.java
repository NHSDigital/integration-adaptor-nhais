package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter
@NoArgsConstructor
public abstract class Reference extends Segment{
    protected abstract String getReference();
    protected abstract String getQualifier();

    @Override
    public String getKey() {
        return "RFF";
    }

    @Override
    public String getValue() {
        return getQualifier() + ":" + getReference();
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (getQualifier().isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }
}
