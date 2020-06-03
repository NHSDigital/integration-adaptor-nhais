package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class PersonSex extends Segment {
    //PDI+1'
    private @NonNull String sexCode;

    @Override
    public String getKey() {
        return "PDI";
    }

    @Override
    public String getValue() {
        return sexCode;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (sexCode.isEmpty() || Objects.isNull(sexCode)) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
    }
}
