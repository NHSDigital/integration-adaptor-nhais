package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BeginningOfMessageTest {

    @Test
    public void testValidBeginningOfMessage() throws EdifactValidationException {
        BeginningOfMessage beginningOfMessage = new BeginningOfMessage();

        String edifact = beginningOfMessage.toEdifact();

        assertEquals("BGM+++507'", edifact);
    }

    @Test
    public void whenParsingEdifactWithNewlines_thenSegmentParsedCorrectly() {
        BeginningOfMessage expected = new BeginningOfMessage();
        BeginningOfMessage beginningOfMessage = BeginningOfMessage.fromEdifact(EdifactFixtures.STATE_ONLY_WITH_NEWLINES);
        assertEquals(expected, beginningOfMessage);
    }

    @Test
    public void whenParsingEdifactWithoutNewlines_thenSegmentParsedCorrectly() {
        BeginningOfMessage expected = new BeginningOfMessage();
        BeginningOfMessage beginningOfMessage = BeginningOfMessage.fromEdifact(EdifactFixtures.STATE_ONLY_WITHOUT_NEWLINES);
        assertEquals(expected, beginningOfMessage);
    }

    @Test
    public void whenParsingEdifactSegmentDoesNotExist_thenThrowsEdifactValidationException() {
        assertThrows(EdifactValidationException.class, () -> BeginningOfMessage.fromEdifact("asdf"));
    }
}
