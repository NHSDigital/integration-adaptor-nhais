package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A specialisation of a segment for the specific use case of an interchange header
 * takes in specific values required to generate an interchange header
 * example: UNB+UNOA:2+TES5+XX11+920113:1317+00000002'
 */
@Getter @Setter @RequiredArgsConstructor
public class InterchangeHeader extends Segment {

    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd:hhmm");

    private @NonNull String sender;
    private @NonNull String recipient;
    private @NonNull ZonedDateTime translationTime;
    private Integer sequenceNumber;

    @Override
    public String getKey() {
        return "UNB";
    }

    @Override
    public String getValue() {
        String timestamp = translationTime.format(DATE_FORMAT);
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return "UNOA:2"+"+"+sender+"+"+recipient+"+"+timestamp+"+"+formattedSequenceNumber;
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

        if(sender.isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute sender is required");
        }
        if(recipient.isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute recipient is required");
        }
    }
}
