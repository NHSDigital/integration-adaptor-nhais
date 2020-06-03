package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 * A specialisation of a segment for the specific use case of a message header
 * takes in specific values required to generate an message header
 * example: UNH+00000003+FHSREG:0:1:FH:FHS001'
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MessageHeader extends Segment {

    public static final String KEY = "UNH";
    private static final long MAX_MESSAGE_SEQUENCE = 99_999_999L;

    private Long sequenceNumber;

    @Override
    public String getKey() {
        return KEY;
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
        if (sequenceNumber < 1 || sequenceNumber > MAX_MESSAGE_SEQUENCE) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber must be between 1 and " + MAX_MESSAGE_SEQUENCE);
        }
    }

    @Override
    public void preValidate() {
        // Do nothing
    }

    public static MessageHeader fromString(String edifactString) {
        if(!edifactString.startsWith(MessageHeader.KEY)){
            throw new IllegalArgumentException("Can't create " + MessageHeader.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = edifactString.split("\\+");
        return new MessageHeader(Long.valueOf(split[1]));
    }

}

