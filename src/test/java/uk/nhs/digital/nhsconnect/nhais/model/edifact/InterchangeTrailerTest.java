package uk.nhs.digital.nhsconnect.nhais.model.edifact;


import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class InterchangeTrailerTest {

    @Test
    public void testValidInterchangeTrailer() throws EdifactValidationException {
        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(1);
        interchangeTrailer.setSequenceNumber(1L);

        String edifact = interchangeTrailer.toEdifact();

        assertEquals("UNZ+1+00000001'", edifact);
    }

    @Test
    public void testPreValidationNumberOfMessagesZero() {
        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(0);
        interchangeTrailer.setSequenceNumber(1L);

        Exception exception = assertThrows(EdifactValidationException.class, interchangeTrailer::preValidate);

        String expectedMessage = "UNZ: Attribute numberOfMessages is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testPreValidationSequenceNumberMissing() {
        InterchangeTrailer interchangeTrailer = new InterchangeTrailer(1);

        Exception exception = assertThrows(EdifactValidationException.class, interchangeTrailer::toEdifact);

        String expectedMessage = "UNZ: Attribute sequenceNumber is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
