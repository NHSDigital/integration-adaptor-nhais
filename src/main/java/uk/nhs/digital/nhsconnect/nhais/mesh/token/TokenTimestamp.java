package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

@RequiredArgsConstructor
class TokenTimestamp {
    private static final String TIMESTAMP_FORMAT= "yyyyMMddHHmm";

    @NonNull private final Instant datetime;

    public String getValue() {
        return DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT).withZone(TimestampService.UKZone).format(datetime);
    }
}
