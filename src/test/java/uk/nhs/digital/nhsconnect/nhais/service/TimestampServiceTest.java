package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimestampServiceTest {
    @Test
    public void whenGettingTimestamp_thenNanosAreZeroed() {
        var instant = new TimestampService().getCurrentTimestamp();

        assertEquals(0, instant.getNano());
    }
}
