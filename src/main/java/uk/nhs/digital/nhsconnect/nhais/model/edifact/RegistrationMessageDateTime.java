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
 * Example DTM+137:199201141619:203'
 * <p>
 * WARNING: Due to a bug in NHAIS the value if this segment when received inbound may not be as expected. The timestamp
 * of the interchange header is a more reliable data point for translation timestamp.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = false)
@ToString
public class RegistrationMessageDateTime extends Segment {

    public static final String KEY = "DTM"; // Date/time/period
    private static final String TYPE_CODE = "137"; // Document/message date/time
    public static final String KEY_QUALIFIER = KEY + "+" + TYPE_CODE;
    private static final String FORMAT_CODE = "203";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        .withZone(TimestampService.UKZone);
    /**
     * When creating a new RegistrationTimestamp the timestamp is not provided. This is considered "stateful" and a
     * value thas is shared across multiple segments. The FhirToEdifactService sets this value as a pre-precessing step
     * just before the segments are translated "toEdifact()"
     */
    private Instant timestamp;

    public static RegistrationMessageDateTime fromString(String edifactString) {
        if (!edifactString.startsWith(KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + RegistrationMessageDateTime.class.getSimpleName() + " from " + edifactString);
        }
        String timestamp = Split.byColon(edifactString)[1];
        Instant instant = ZonedDateTime.parse(timestamp, DATE_TIME_FORMAT).toInstant();
        return new RegistrationMessageDateTime(instant);
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
