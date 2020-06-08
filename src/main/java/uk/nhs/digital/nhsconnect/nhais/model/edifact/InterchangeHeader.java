package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A specialisation of a segment for the specific use case of an interchange header
 * takes in specific values required to generate an interchange header
 * example: UNB+UNOA:2+TES5+XX11+920113:1317+00000002'
 */
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor
public class InterchangeHeader extends Segment {

    public static final String KEY = "UNB";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd:HHmm").withZone(TimestampService.UKZone);
    private static final long MAX_INTERCHANGE_SEQUENCE = 99_999_999L;

    private @NonNull String sender;
    private @NonNull String recipient;
    private @NonNull Instant translationTime;
    private Long sequenceNumber;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        String timestamp = DATE_FORMAT.format(translationTime);
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return "UNOA:2" + "+" + sender + "+" + recipient + "+" + timestamp + "+" + formattedSequenceNumber;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (sequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if (sequenceNumber < 1 || sequenceNumber > MAX_INTERCHANGE_SEQUENCE) {
            throw new EdifactValidationException(
                getKey() + ": Attribute sequenceNumber must be between 1 and " + MAX_INTERCHANGE_SEQUENCE);
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

    public static InterchangeHeader fromString(String edifactString) {
        if(!edifactString.startsWith(InterchangeHeader.KEY)){
            throw new IllegalArgumentException("Can't create " + InterchangeHeader.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = edifactString.split("\\+");
        ZonedDateTime translationTime = ZonedDateTime.parse(split[4], DateTimeFormatter.ofPattern("yyMMdd:HHmm").withZone(TimestampService.UKZone));
        return new InterchangeHeader(split[2], split[3], translationTime.toInstant(), Long.valueOf(split[5]));
    }
}
