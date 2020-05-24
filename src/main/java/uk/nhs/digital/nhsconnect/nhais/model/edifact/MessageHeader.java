package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A specialisation of a segment for the specific use case of a message header
 * takes in specific values required to generate an message header
 * example: UNH+00000003+FHSREG:0:1:FH:FHS001'
 */
@Getter @Setter @RequiredArgsConstructor
public class MessageHeader extends Segment {

    private Integer sequenceNumber;

    @Override
    public String getKey() {
        return "UNH";
    }

    @Override
    public String getValue() {
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return formattedSequenceNumber+"+FHSREG:0:1:FH:FHS001";
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (sequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if(sequenceNumber != 3){
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
    }

    @Override
    public void preValidate() {
        // Do nothing
    }

}

