package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

@Getter
@Setter
@RequiredArgsConstructor
public class RecepMessageHeader extends Segment {
    private Long sequenceNumber;

    @Override
    public String getKey() {
        return "UNH";
    }

    @Override
    public String getValue() {
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return formattedSequenceNumber + "+FHSREG:0:1:FH";
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (sequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if (sequenceNumber <= 0) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber must be greater than or equal to 1");
        }
    }

    @Override
    public void preValidate() {
        // Do nothing
    }
}

