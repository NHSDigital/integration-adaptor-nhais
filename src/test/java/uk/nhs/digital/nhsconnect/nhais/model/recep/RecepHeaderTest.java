package uk.nhs.digital.nhsconnect.nhais.model.recep;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecepHeaderTest {
    private final static String SENDER = "TES5";
    private final static String RECIPIENT = "XX11";
    private final static Long SERIAL = 231L;

    private static final ZonedDateTime DATE_TIME = ZonedDateTime.of(
            2020,
            5,
            25,
            10,
            20,
            0,
            0,
            ZoneId.of("GMT"));

    private final static RecepHeader RECEP_HEADER = new RecepHeader(
            SENDER,
            RECIPIENT,
            DATE_TIME,
            SERIAL);


    @Test
    public void When_RecepHeaderIsCreated_EdifactIsCorrect() {
        assertEquals(RECEP_HEADER.toEdifact(),
                "UNB+UNOA:2+TES5+XX11+200525:1020+00000231++RECEP+++EDIFACT TRANSFER'");
    }
}