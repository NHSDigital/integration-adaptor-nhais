package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class TokenTimestampTest {

    private final static Instant FIXED_TIME_LOCAL = LocalDate.of(1991, 11, 6)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant();


    @Test
    void testTimestampIsInCorrectFormat() {
        String formattedDateTime = new TokenTimestamp(FIXED_TIME_LOCAL).getValue();
        assertThat(formattedDateTime).isEqualTo("199111060000"); //yyyyMMddHHmm
    }
}