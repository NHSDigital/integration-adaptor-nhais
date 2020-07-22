package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Example DTM+962:19920725:102'
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class FP69ExpiryDate extends Segment {
    private final static String KEY = "DTM";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(TimestampService.UKZone);
    private final static String QUALIFIER = "962";
    public final static String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
    private final static String DATE_FORMAT = "102";

    private final @NonNull LocalDate expiryDate;

    public static FP69ExpiryDate fromString(String edifactString) {
        if (!edifactString.startsWith(FP69ExpiryDate.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + FP69ExpiryDate.class.getSimpleName() + " from " + edifactString);
        }
        var dateStr = Split.byColon(Split.byPlus(edifactString)[1])[1];
        var date = LocalDate.parse(dateStr, DATE_TIME_FORMATTER);
        return new FP69ExpiryDate(date);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER
            .concat(COLON_SEPARATOR)
            .concat(DATE_TIME_FORMATTER.format(expiryDate))
            .concat(COLON_SEPARATOR)
            .concat(DATE_FORMAT);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (expiryDate == null) {
            throw new EdifactValidationException(getKey() + ": Expiry date is required");
        }
    }
}
