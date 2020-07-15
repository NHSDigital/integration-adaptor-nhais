package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Example DTM+962:19920725:102'
 */
@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class FP69ExpiryDate extends Segment {
    private final static String KEY = "DTM";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");//.withZone(TimestampService.UKZone);
    private final static String QUALIFIER = "962";
    public final static String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
    private final static String DATE_FORMAT = "102";

    private @NonNull Instant timestamp;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER
            .concat(COLON_SEPARATOR)
            .concat(DATE_TIME_FORMATTER.format(timestamp))
            .concat(COLON_SEPARATOR)
            .concat(DATE_FORMAT);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (timestamp == null) {
            throw new EdifactValidationException(getKey() + ": Expiry date is required");
        }
    }

    public static FP69ExpiryDate fromString(String edifactString) {
        if (!edifactString.startsWith(FP69ExpiryDate.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + FP69ExpiryDate.class.getSimpleName() + " from " + edifactString);
        }
        var dateTime = Split.byColon(Split.byPlus(edifactString)[1])[1];
        var instant = LocalDate.parse(dateTime, DATE_TIME_FORMATTER).atStartOfDay(TimestampService.UKZone).toInstant();
        return new FP69ExpiryDate(instant);
    }
}
