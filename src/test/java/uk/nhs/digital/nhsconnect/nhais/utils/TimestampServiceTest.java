package uk.nhs.digital.nhsconnect.nhais.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TimestampServiceTest {

    private static final int MILLISECOND_MULTIPLIER = 1000000;
    private static final int EPOCH_SECONDS = 123123;

    @Test
    public void When_GettingTimestamp_Expect_PrecisionIsMilliseconds() {
        var instant = new TimestampService().getCurrentTimestamp();
        long remainder = instant.getNano() % MILLISECOND_MULTIPLIER; // nanoseconds per millisecond

        assertThat(remainder).isEqualTo(0);
    }

    @Test
    public void When_FormattingInISO_Expect_ISOForUKZoneIsReturned() {
        Instant timestamp = Instant.ofEpochSecond(EPOCH_SECONDS);

        assertThat(new TimestampService().formatInISO(timestamp)).isEqualTo("1970-01-02T11:12:03+01:00[Europe/London]");
    }
}
