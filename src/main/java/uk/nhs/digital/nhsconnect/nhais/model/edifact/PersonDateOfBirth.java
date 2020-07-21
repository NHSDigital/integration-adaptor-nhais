package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class PersonDateOfBirth extends Segment {
    //DTM+329:19911106:102'
    private final static String KEY = "DTM";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(TimestampService.UKZone);
    private final static String QUALIFIER = "329";
    public final static String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
    private final static String DATE_FORMAT = "102";
    private @NonNull LocalDate dateOfBirth;

    public static PersonDateOfBirth fromString(String edifactString) {
        if (!edifactString.startsWith(PersonDateOfBirth.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + PersonDateOfBirth.class.getSimpleName() + " from " + edifactString);
        }
        var dateTime = Split.byColon(Split.byPlus(edifactString)[1])[1];
        var instant = LocalDate.parse(dateTime, DATE_TIME_FORMATTER);
        return new PersonDateOfBirth(instant);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER
            .concat(COLON_SEPARATOR)
            .concat(DATE_TIME_FORMATTER.format(dateOfBirth))
            .concat(COLON_SEPARATOR)
            .concat(DATE_FORMAT);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (dateOfBirth == null) {
            throw new EdifactValidationException(getKey() + ": Date of birth is required");
        }
    }
}
