package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Example DTM+958:19920113:102'
 */
@RequiredArgsConstructor
public class PersonDateOfExit extends Segment {

    private final static String KEY = "DTM";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(TimestampService.UKZone);
    private final static String QUALIFIER = "958";
    private final static String DATE_FORMAT = "102";
    private final @NonNull LocalDate dateOfExit;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER
            .concat(COLON_SEPARATOR)
            .concat(DATE_TIME_FORMATTER.format(dateOfExit))
            .concat(COLON_SEPARATOR)
            .concat(DATE_FORMAT);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(dateOfExit)) {
            throw new EdifactValidationException(getKey() + ": Date of exit is required");
        }
    }
}
