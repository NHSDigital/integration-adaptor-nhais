package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageHeaderTest {
    private static final long SEQUENCE_NUMBER_OUT_OF_UPPER_BOUND = 100_000_000L;
    private static final long MAX_SEQUENCE_NUMBER = 99_999_999L;

    @Test
    public void testValidMessageHeader() throws EdifactValidationException {
        final long sequenceNumber = 3L;
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setSequenceNumber(sequenceNumber);

        String edifact = messageHeader.toEdifact();

        assertEquals("UNH+00000003+FHSREG:0:1:FH:FHS001'", edifact);
    }

    @Test
    public void testValidationStateful() {
        MessageHeader messageHeader = new MessageHeader();

        assertThatThrownBy(messageHeader::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNH: Attribute sequenceNumber is required");
    }

    @Test
    public void testValidationStatefulMinMaxSequenceNumber() throws EdifactValidationException {
        final long sequenceNumber = 1L;
        var messageHeader = new MessageHeader();

        messageHeader.setSequenceNumber(0L);
        assertThatThrownBy(messageHeader::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNH: Attribute sequenceNumber must be between 1 and 99999999");

        messageHeader.setSequenceNumber(SEQUENCE_NUMBER_OUT_OF_UPPER_BOUND);
        assertThatThrownBy(messageHeader::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNH: Attribute sequenceNumber must be between 1 and 99999999");

        messageHeader.setSequenceNumber(sequenceNumber);
        messageHeader.validateStateful();

        messageHeader.setSequenceNumber(MAX_SEQUENCE_NUMBER);
        messageHeader.validateStateful();
    }

    @Test
    void testFromString() {
        final long sequenceNumber = 3L;
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setSequenceNumber(sequenceNumber);

        assertThat(MessageHeader.fromString("UNH+00000003+FHSREG:0:1:FH:FHS001").getValue()).isEqualTo(messageHeader.getValue());
        assertThatThrownBy(() -> MessageHeader.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
