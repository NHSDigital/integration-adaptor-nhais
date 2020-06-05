package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeginningOfMessageTest {
    @Test
    public void testValidBeginningOfMessage() throws EdifactValidationException {
        BeginningOfMessage beginningOfMessage = new BeginningOfMessage();

        String edifact = beginningOfMessage.toEdifact();

        assertEquals("BGM+++507'", edifact);
    }
}
