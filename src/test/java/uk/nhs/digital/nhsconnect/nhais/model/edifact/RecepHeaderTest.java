package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RecepHeaderTest {
    private static final Instant DATE_TIME = ZonedDateTime
        .of(LocalDateTime.parse("2019-03-23T09:00:00"), ZoneOffset.UTC)
        .toInstant();
    private final RecepHeader recepHeader = new RecepHeader("SNDR", "RECP", DATE_TIME).setSequenceNumber(1L);

    @Test
    void toEdifactTest() {
        String edifact = recepHeader.toEdifact();

        assertThat(edifact).isEqualTo("UNB+UNOA:2+SNDR+RECP+190323:0900+00000001++RECEP+++EDIFACT TRANSFER'");
    }
}
