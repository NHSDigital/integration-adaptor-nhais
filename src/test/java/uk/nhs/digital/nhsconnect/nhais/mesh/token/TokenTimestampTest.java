package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import org.junit.jupiter.api.Test;

class TokenTimestampTest {

    private static final Instant FIXED_TIME_LOCAL = ZonedDateTime
        .of(LocalDateTime.parse("1991-11-06T12:30:00"), TimestampService.UK_ZONE)
        .toInstant();

    @Test
    void testTimestampIsInCorrectFormat() {
        String formattedDateTime = new TokenTimestamp(FIXED_TIME_LOCAL).getValue();
        assertThat(formattedDateTime).isEqualTo("199111061230"); //yyyyMMddHHmm
    }
}