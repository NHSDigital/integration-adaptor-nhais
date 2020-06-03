package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TimestampServiceTest {
    @Test
    public void whenGettingTimestamp_thenNanosAreZeroed() {
        var instant = new TimestampService().getCurrentTimestamp();

        assertThat(instant.getNano()).isEqualTo(0);
    }

    @Test
    public void whenFormattingInISO_thenISOForUKZoneIsReturned() {
        Instant timestamp = Instant.ofEpochSecond(123123);

        assertThat(new TimestampService().formatInISO(timestamp)).isEqualTo("1970-01-02T11:12:03+01:00[Europe/London]");
    }
}
