package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZonedDateTime;

import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import org.junit.jupiter.api.Test;

class TokenTimestampTest {

    private final static Instant FIXED_TIME_LOCAL = ZonedDateTime.of(1991,11,6,12,30,0,0, TimestampService.UKZone)
        .toInstant();

    @Test
    void testTimestampIsInCorrectFormat() {
        String formattedDateTime = new TokenTimestamp(FIXED_TIME_LOCAL).getValue();
        assertThat(formattedDateTime).isEqualTo("199111061230"); //yyyyMMddHHmm
    }
}