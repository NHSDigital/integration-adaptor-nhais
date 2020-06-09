package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTrailerTest {

    @Test
    public void testValidMessageHeader() throws EdifactValidationException {
        MessageTrailer messageTrailer = new MessageTrailer(18);
        messageTrailer.setSequenceNumber(3L);

        String edifact = messageTrailer.toEdifact();

        assertEquals("UNT+18+00000003'", edifact);
    }

    @Test
    public void testValidationStatefulNonSequenceNumber() {
        MessageTrailer messageTrailer = new MessageTrailer(18);

        Exception exception = assertThrows(EdifactValidationException.class, messageTrailer::validateStateful);

        String expectedMessage = "UNT: Attribute sequenceNumber is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testValidationStatefulInvalidNumberOfSegments() {
        MessageTrailer messageTrailer = new MessageTrailer(-1);
        messageTrailer.setSequenceNumber(3L);

        Exception exception = assertThrows(EdifactValidationException.class, messageTrailer::validateStateful);

        String expectedMessage = "UNT: Attribute numberOfSegments must be greater than or equal to 2";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testFromString() {
        var expectedMessageTrailer = new MessageTrailer(18).setSequenceNumber(3L);
        var edifact = "UNT+18+00000003'";

        var messageTrailer = MessageTrailer.fromString(edifact);

        assertThat(messageTrailer).isExactlyInstanceOf(MessageTrailer.class);
        assertThat(messageTrailer).isEqualToComparingFieldByField(expectedMessageTrailer);
        assertThat(messageTrailer.toEdifact()).isEqualTo(edifact);
    }
}
