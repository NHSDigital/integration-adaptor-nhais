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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class PersonDateOfBirth extends Segment {
    //DTM+329:19911106:102'
    private final static String KEY = "DTM";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(TimestampService.UKZone);
    private final static String QUALIFIER = "329";
    private final static String DATE_FORMAT = "102";
    public final static String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
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
        if (Objects.isNull(timestamp)) {
            throw new EdifactValidationException(getKey() + ": Date of birth is required");
        }
    }

    public static PersonDateOfBirth fromString(String edifactString) {
        if (!edifactString.startsWith(PersonDateOfBirth.KEY)) {
            throw new IllegalArgumentException("Can't create " + PersonDateOfBirth.class.getSimpleName() + " from " + edifactString);
        }

        return PersonDateOfBirth.builder().timestamp(LocalDateTime.of(LocalDate
            .parse(Split.byColon(edifactString)[1], DateTimeFormatter.BASIC_ISO_DATE), LocalTime.MIDNIGHT)
            .toInstant(ZoneOffset.UTC)).build();
    }
}
