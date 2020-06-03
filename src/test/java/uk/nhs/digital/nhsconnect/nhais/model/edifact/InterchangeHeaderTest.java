package uk.nhs.digital.nhsconnect.nhais.model.edifact;


import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InterchangeHeaderTest {

    private final Instant TRANSLATION_WINTER_DATE_TIME = ZonedDateTime
        .of(2019, 3, 23, 9, 0, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final Instant TRANSLATION_SUMMER_DATE_TIME = ZonedDateTime
        .of(2019, 5, 23, 9, 0, 0, 0, ZoneOffset.UTC)
        .toInstant();
    private final InterchangeHeader interchangeHeaderWinter = new InterchangeHeader("SNDR", "RECP", TRANSLATION_WINTER_DATE_TIME).setSequenceNumber(1L);
    private final InterchangeHeader interchangeHeaderSummer = new InterchangeHeader("SNDR", "RECP", TRANSLATION_SUMMER_DATE_TIME).setSequenceNumber(1L);

    @Test
    public void testValidInterchangeHeaderWithWinterTime() throws EdifactValidationException {
        String edifact = interchangeHeaderWinter.toEdifact();

        assertThat(edifact).isEqualTo("UNB+UNOA:2+SNDR+RECP+190323:0900+00000001'");
    }

    @Test
    public void testValidInterchangeHeaderWithSummerTime() throws EdifactValidationException {
        String edifact = interchangeHeaderSummer.toEdifact();

        assertThat(edifact).isEqualTo("UNB+UNOA:2+SNDR+RECP+190523:1000+00000001'");
    }

    @Test
    public void testValidationStateful() {
        InterchangeHeader emptySequenceNumber = new InterchangeHeader("SNDR", "RECP", TRANSLATION_SUMMER_DATE_TIME);
        assertThatThrownBy(emptySequenceNumber::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sequenceNumber is required");
    }

    @Test
    public void testValidationStatefulMinMaxSequenceNumber() throws EdifactValidationException {
        var interchangeHeader = new InterchangeHeader("SNDR", "RECP", TRANSLATION_SUMMER_DATE_TIME);

        interchangeHeader.setSequenceNumber(0L);
        assertThatThrownBy(interchangeHeader::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sequenceNumber must be between 1 and 99999999");

        interchangeHeader.setSequenceNumber(100_000_000L);
        assertThatThrownBy(interchangeHeader::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sequenceNumber must be between 1 and 99999999");

        interchangeHeader.setSequenceNumber(1L);
        interchangeHeader.validateStateful();

        interchangeHeader.setSequenceNumber(99_999_999L);
        interchangeHeader.validateStateful();
    }

    @Test
    public void testPreValidationSenderEmptyString() {
        InterchangeHeader emptySender = new InterchangeHeader("", "RECP", TRANSLATION_SUMMER_DATE_TIME).setSequenceNumber(1L);
        assertThatThrownBy(emptySender::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sender is required");
    }

    @Test
    public void testPreValidationRecipientEmptyString() {
        InterchangeHeader emptyRecipient = new InterchangeHeader("SNDR", "", TRANSLATION_SUMMER_DATE_TIME).setSequenceNumber(1L);
        assertThatThrownBy(emptyRecipient::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute recipient is required");
    }

    @Test
    void testFromString() {
        assertThat(InterchangeHeader.fromString("UNB+UNOA:2+SNDR+RECP+190323:0900+00000001").getValue()).isEqualTo(interchangeHeaderWinter.getValue());
        assertThatThrownBy(() -> InterchangeHeader.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
