package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Example DTM+957:19920113:102'
 */
@RequiredArgsConstructor
public class PersonDateOfEntry extends Segment {

    private static final String KEY = "DTM";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(TimestampService.UKZone);
    private static final String QUALIFIER = "957";
    private static final String DATE_FORMAT = "102";
    private final @NonNull LocalDate dateOfEntry;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER
            .concat(COLON_SEPARATOR)
            .concat(DATE_TIME_FORMATTER.format(dateOfEntry))
            .concat(COLON_SEPARATOR)
            .concat(DATE_FORMAT);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(dateOfEntry)) {
            throw new EdifactValidationException(getKey() + ": Date of entry is required");
        }
    }
}
