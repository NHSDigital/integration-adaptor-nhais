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

    public static final String KEY = "UNT";
    private @NonNull Integer numberOfSegments;
    private Long sequenceNumber;

    @Override
    public String getKey() {
        return KEY;
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
        if(sequenceNumber <= 0){
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber must be greater than or equal to zero");
        }
        if(numberOfSegments <= 1){
            throw new EdifactValidationException(getKey() + ": Attribute numberOfSegments must be greater than or equal to 2");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        //Do nothing
    }

    public static MessageTrailer fromString(String edifactString) {
        return null;
    }
}
