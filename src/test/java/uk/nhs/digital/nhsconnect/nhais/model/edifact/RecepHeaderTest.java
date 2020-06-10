package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RecepHeaderTest {
    private final Instant DATE_TIME = ZonedDateTime
        .of(2019, 3, 23, 9, 0, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final RecepHeader recepHeader = new RecepHeader("SNDR", "RECP", DATE_TIME).setSequenceNumber(1L);

    @Test
    void toEdifactTest() {
        String edifact = recepHeader.toEdifact();

        assertThat(edifact).isEqualTo("UNB+UNOA:2+SNDR+RECP+190323:0900+00000001++RECEP+++EDIFACT TRANSFER'");
    }
}
