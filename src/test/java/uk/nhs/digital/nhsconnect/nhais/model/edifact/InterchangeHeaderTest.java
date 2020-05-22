package uk.nhs.digital.nhsconnect.nhais.model.edifact;


import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterchangeHeaderTest {

    @Test
    public void testValidInterchangeHeader() {
        ZonedDateTime translationDateTime = ZonedDateTime.of(2019, 4, 23, 9, 0, 0, 0, ZoneOffset.UTC);
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", translationDateTime);
        interchangeHeader.setSequenceNumber(1);

        String edifact = interchangeHeader.toEdifact();

        assertEquals("UNB+UNOA:2+SNDR+RECP+190423:0900+00000001'", edifact);
    }

    // TODO: test validation errors

}
