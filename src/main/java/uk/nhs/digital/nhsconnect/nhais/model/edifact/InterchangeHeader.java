package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * A specialisation of a segment for the specific use case of an interchange header
 * takes in specific values required to generate an interchange header
 * example: UNB+UNOA:2+TES5+XX11+920113:1317+00000002'
 */
@Getter @Setter @RequiredArgsConstructor
public class InterchangeHeader extends Segment {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd:HHmm").withZone(TimestampService.UKZone);

    private @NonNull String sender;
    private @NonNull String recipient;
    private @NonNull Instant translationTime;
    private Long sequenceNumber;

    @Override
    public String getKey() {
        return "UNB";
    }

    @Override
    public String getValue() {
        String timestamp = DATE_FORMAT.format(translationTime);
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return "UNOA:2"+"+"+sender+"+"+recipient+"+"+timestamp+"+"+formattedSequenceNumber;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (sequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if(sequenceNumber <= 0){
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber must be greater than or equal to 1");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (sender.isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute sender is required");
        }
        if (recipient.isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute recipient is required");
        }
    }
}
