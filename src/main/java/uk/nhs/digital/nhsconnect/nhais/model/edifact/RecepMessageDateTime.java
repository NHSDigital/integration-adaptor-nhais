package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representation of the timestamp DTM segment used in RECEP messages
 * <p>
 * WARNING: Due to a bug in NHAIS the value if this segment when received inbound may not be as expected. The timestamp
 * * of the interchange header is a more reliable data point for translation timestamp.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = false)
@ToString
public class RecepMessageDateTime extends Segment {

    public static final String KEY = "DTM"; // Date/time/period
    private static final String TYPE_CODE = "815"; // receive date of interchange
    public static final String KEY_QUALIFIER = KEY + "+" + TYPE_CODE;
    private static final String FORMAT_CODE = "306";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        .withZone(TimestampService.UKZone);
    /**
     * When creating a new RecepTimestamp the timestamp is not provided. This is considered "stateful" and a value
     * that is shared across multiple segments. For outbound registration messages the RecepProducerService sets this
     * value as a pre-precessing step just before the segments are translated "toEdifact()"
     */
    private Instant timestamp;

    public static RecepMessageDateTime fromString(String edifactString) {
        if (!edifactString.startsWith(KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + RecepMessageDateTime.class.getSimpleName() + " from " + edifactString);
        }
        String timestamp = Split.byColon(edifactString)[1];
        Instant instant = ZonedDateTime.parse(timestamp, DATE_TIME_FORMAT).toInstant();
        return new RecepMessageDateTime(instant);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return TYPE_CODE + ":" + DATE_TIME_FORMAT.format(this.timestamp) + ":" + FORMAT_CODE;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (timestamp == null) {
            throw new EdifactValidationException(getKey() + ": Attribute timestamp is required");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        // nothing
    }
}
