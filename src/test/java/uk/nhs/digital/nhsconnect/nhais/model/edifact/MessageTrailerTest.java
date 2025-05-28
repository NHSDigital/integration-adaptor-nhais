package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTrailerTest {

    private static final int NUMBER_OF_SEGMENTS = 18;
    private static final long SEQUENCE_NUMBER = 3L;

    @Test
    public void testValidMessageHeader() throws EdifactValidationException {
        MessageTrailer messageTrailer = new MessageTrailer(NUMBER_OF_SEGMENTS);
        messageTrailer.setSequenceNumber(SEQUENCE_NUMBER);

        String edifact = messageTrailer.toEdifact();

        assertEquals("UNT+18+00000003'", edifact);
    }

    @Test
    public void testValidationStatefulNonSequenceNumber() {
        MessageTrailer messageTrailer = new MessageTrailer(NUMBER_OF_SEGMENTS);

        Exception exception = assertThrows(EdifactValidationException.class, messageTrailer::validateStateful);

        String expectedMessage = "UNT: Attribute sequenceNumber is required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testValidationStatefulInvalidNumberOfSegments() {
        MessageTrailer messageTrailer = new MessageTrailer(-1);
        messageTrailer.setSequenceNumber(SEQUENCE_NUMBER);

        Exception exception = assertThrows(EdifactValidationException.class, messageTrailer::validateStateful);

        String expectedMessage = "UNT: Attribute numberOfSegments must be greater than or equal to 2";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testFromString() {
        var expectedMessageTrailer = new MessageTrailer(NUMBER_OF_SEGMENTS).setSequenceNumber(SEQUENCE_NUMBER);
        var edifact = "UNT+18+00000003'";

        var messageTrailer = MessageTrailer.fromString(edifact);

        assertThat(messageTrailer).isExactlyInstanceOf(MessageTrailer.class);
        assertThat(messageTrailer).isEqualToComparingFieldByField(expectedMessageTrailer);
        assertThat(messageTrailer.toEdifact()).isEqualTo(edifact);
    }
}
