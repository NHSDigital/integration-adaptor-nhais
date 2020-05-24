package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageHeaderTest {

    @Test
    public void testValidMessageHeader() throws EdifactValidationException {
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setSequenceNumber(3);

        String edifact = messageHeader.toEdifact();

        assertEquals("UNH+00000003+FHSREG:0:1:FH:FHS001'", edifact);
    }

    @Test
    public void testValidationStateful() {
        MessageHeader messageHeader = new MessageHeader();

        Exception exception = assertThrows(EdifactValidationException.class, messageHeader::validateStateful);

        String expectedMessage = "UNH: Attribute sequenceNumber is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
