package uk.nhs.digital.nhsconnect.nhais.model.edifact;


import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InterchangeHeaderTest {

    private final ZonedDateTime translationDateTime = ZonedDateTime.of(2019, 4, 23, 9, 0, 0, 0, ZoneOffset.UTC);

    @Test
    public void testValidInterchangeHeader() throws EdifactValidationException {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", translationDateTime);
        interchangeHeader.setSequenceNumber(1);

        String edifact = interchangeHeader.toEdifact();

        assertEquals("UNB+UNOA:2+SNDR+RECP+190423:0900+00000001'", edifact);
    }

    @Test
    public void testValidationStateful() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", translationDateTime);

        Exception exception = assertThrows(EdifactValidationException.class, interchangeHeader::validateStateful);

        String expectedMessage = "UNB: Attribute sequenceNumber is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testPreValidationSenderEmptyString() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("", "RECP", translationDateTime);

        Exception exception = assertThrows(EdifactValidationException.class, interchangeHeader::preValidate);

        String expectedMessage = "UNB: Attribute sender is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testPreValidationRecipientEmptyString() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "", translationDateTime);

        Exception exception = assertThrows(EdifactValidationException.class, interchangeHeader::preValidate);

        String expectedMessage = "UNB: Attribute recipient is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
