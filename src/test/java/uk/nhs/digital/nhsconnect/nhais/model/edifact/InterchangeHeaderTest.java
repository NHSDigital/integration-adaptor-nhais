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

    @Test
    public void testValidInterchangeHeaderWithWinterTime() throws EdifactValidationException {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", TRANSLATION_WINTER_DATE_TIME);
        interchangeHeader.setSequenceNumber(1L);

        String edifact = interchangeHeader.toEdifact();

        assertThat(edifact).isEqualTo("UNB+UNOA:2+SNDR+RECP+190323:0900+00000001'");
    }

    @Test
    public void testValidInterchangeHeaderWithSummerTime() throws EdifactValidationException {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", TRANSLATION_SUMMER_DATE_TIME);
        interchangeHeader.setSequenceNumber(1L);

        String edifact = interchangeHeader.toEdifact();

        assertThat(edifact).isEqualTo("UNB+UNOA:2+SNDR+RECP+190523:1000+00000001'");
    }

    @Test
    public void testValidationStateful() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", TRANSLATION_WINTER_DATE_TIME);

        assertThatThrownBy(interchangeHeader::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sequenceNumber is required");
    }

    @Test
    public void testPreValidationSenderEmptyString() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("", "RECP", TRANSLATION_WINTER_DATE_TIME);

        assertThatThrownBy(interchangeHeader::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sender is required");
    }

    @Test
    public void testPreValidationRecipientEmptyString() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "", TRANSLATION_WINTER_DATE_TIME);

        assertThatThrownBy(interchangeHeader::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute recipient is required");
    }
}
