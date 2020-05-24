package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 * A specialisation of a segment for the specific use case of a message trailer
 * takes in specific values required to generate a message trailer
 * example: UNT+18+00000003'
 */
@Getter @Setter @RequiredArgsConstructor
public class MessageTrailer extends Segment{

    private @NonNull Integer numberOfSegments;
    private Integer sequenceNumber;

    @Override
    public String getKey() {
        return "UNT";
    }

    @Override
    public String getValue() {
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return numberOfSegments + "+" + formattedSequenceNumber;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (sequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if(sequenceNumber != 3){
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if(numberOfSegments != 18){
            throw new EdifactValidationException(getKey() + ": Attribute numberOfSegments is required");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        //Do nothing
    }
}
