package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class RecepBeginningOfMessage extends Segment {
    private final static String DATE_TIME_FORMAT = "yyyyMMddHHmm";
    private final static String BGM_PREFIX = "+600+243:";
    private final static String BGM_SUFFIX = ":306+64";

    private @NonNull Instant timestamp;

    @Override
    public String getKey() {
        return "BGM";
    }

    @Override
    public String getValue() {
        return BGM_PREFIX
            .concat(getDateTimeFormat().format(timestamp))
            .concat(BGM_SUFFIX);
    }

    @Override
    protected void validateStateful() {
        // Do nothing
    }

    @Override
    public void preValidate() {
        // Do nothing
    }

    private DateTimeFormatter getDateTimeFormat() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(TimestampService.UKZone);
    }
}
