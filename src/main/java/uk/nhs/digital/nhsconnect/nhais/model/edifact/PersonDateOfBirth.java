package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Builder
@Data
public class PersonDateOfBirth extends Segment {
    //DTM+329:19911106:102'
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(TimestampService.UKZone);
    private final static String DOB_PREFIX = "329:";
    private final static String DOB_SUFFIX = ":102";
    private @NonNull Instant timestamp;

    @Override
    public String getKey() {
        return "DTM";
    }

    @Override
    public String getValue() {
        return DOB_PREFIX
            .concat(DATE_TIME_FORMATTER.format(timestamp))
            .concat(DOB_SUFFIX);
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
}
