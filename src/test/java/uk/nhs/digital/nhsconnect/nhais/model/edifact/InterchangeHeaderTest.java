package uk.nhs.digital.nhsconnect.nhais.model.edifact;


import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InterchangeHeaderTest {

    private final Instant translationDateTime = ZonedDateTime
        .of(2019, 4, 23, 9, 0, 0, 0, TimestampService.UKZone)
        .toInstant();

    @Test
    public void testValidInterchangeHeader() throws EdifactValidationException {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", translationDateTime);
        interchangeHeader.setSequenceNumber(1L);

        String edifact = interchangeHeader.toEdifact();

        assertThat(edifact).isEqualTo("UNB+UNOA:2+SNDR+RECP+190423:0900+00000001'");
    }

    @Test
    public void testValidationStateful() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "RECP", translationDateTime);

        assertThatThrownBy(interchangeHeader::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sequenceNumber is required");
    }

    @Test
    public void testPreValidationSenderEmptyString() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("", "RECP", translationDateTime);

        assertThatThrownBy(interchangeHeader::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute sender is required");
    }

    @Test
    public void testPreValidationRecipientEmptyString() {
        InterchangeHeader interchangeHeader = new InterchangeHeader("SNDR", "", translationDateTime);

        assertThatThrownBy(interchangeHeader::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("UNB: Attribute recipient is required");
    }
}
