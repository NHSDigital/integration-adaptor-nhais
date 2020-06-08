package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@RequiredArgsConstructor
public class RecepHeader extends Segment {
    private final static String DATE_TIME_FORMAT = "yyMMdd:HHmm";
    private static String UNOA = "UNOA:2";
    private static String TRANSFER = "+RECEP+++EDIFACT TRANSFER";
    private static String PLUS_SEPARATOR = "+";

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
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return UNOA
                .concat(PLUS_SEPARATOR)
                .concat(sender)
                .concat(PLUS_SEPARATOR)
                .concat(recipient)
                .concat(PLUS_SEPARATOR)
                .concat(getDateTimeFormat().format(translationTime))
                .concat(PLUS_SEPARATOR)
                .concat(formattedSequenceNumber)
                .concat(PLUS_SEPARATOR)
                .concat(TRANSFER);
    }

    private DateTimeFormatter getDateTimeFormat() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(TimestampService.UKZone);
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
    public void preValidate() throws EdifactValidationException {

        if (sender.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute sender is required");
        }
        if (recipient.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute recipient is required");
        }
    }
}
