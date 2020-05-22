package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 * A specialisation of a segment for the specific use case of an interchange trailer
 * takes in specific values required to generate an interchange trailer
 * example: UNZ+1+00000002'
 */
@Getter @Setter @RequiredArgsConstructor
public class InterchangeTrailer extends Segment {

    private @NonNull Integer numberOfMessages;
    private Long sequenceNumber;

    @Override
    public String getKey() {
        return "UNZ";
    }

    @Override
    public String getValue() {
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return numberOfMessages+"+"+formattedSequenceNumber;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (sequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if(sequenceNumber <= 0){
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if(numberOfMessages != 1){
            throw new EdifactValidationException(getKey() + ": Attribute numberOfMessages is required");
        }
    }
}
