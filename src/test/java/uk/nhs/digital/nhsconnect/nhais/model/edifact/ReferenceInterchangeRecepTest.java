package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class ReferenceInterchangeRecepTest {

    private static final long INTERCHANGE_SEQUENCE_NUMBER = 123L;
    private static final int MESSAGE_COUNT = 3;

    @Test
    void When_GettingKey_Expect_ReturnsProperValue() {
        String key = new ReferenceInterchangeRecep(
            INTERCHANGE_SEQUENCE_NUMBER, ReferenceInterchangeRecep.RecepCode.RECEIVED, MESSAGE_COUNT)
            .getKey();

        assertThat(key).isEqualTo("RFF");
    }

    @Test
    void When_GettingValue_Expect_ReturnsProperValue() {
        String value = new ReferenceInterchangeRecep(
            INTERCHANGE_SEQUENCE_NUMBER, ReferenceInterchangeRecep.RecepCode.RECEIVED, MESSAGE_COUNT)
            .getValue();

        assertThat("RIS:00000123 OK:3").isEqualTo(value);
    }

    @Test
    void When_PreValidatedDataViolatesNullChecks_Expect_ThrowsException(SoftAssertions softly) {
        softly.assertThatThrownBy(
            () -> new ReferenceInterchangeRecep(null, ReferenceInterchangeRecep.RecepCode.RECEIVED, MESSAGE_COUNT)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute messageSequenceNumber is required");

        softly.assertThatThrownBy(
            () -> new ReferenceInterchangeRecep(INTERCHANGE_SEQUENCE_NUMBER, null, MESSAGE_COUNT)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute recepCode is required");

        softly.assertThatThrownBy(
            () -> new ReferenceInterchangeRecep(INTERCHANGE_SEQUENCE_NUMBER, ReferenceInterchangeRecep.RecepCode.RECEIVED, null)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute messageCount is required");
    }

    @Test
    void When_Parsing_Expect_RecepCreated() {
        final long expectedReceivedInterchangeSequenceNumber = 5L;
        final long expectedInvalidDataInterchangeSequenceNumber = 10000006L;
        final long expectedNoValidDataInterchangeSequenceNumber = 99000006L;
        final int expectedReceivedMessageCount = 4;
        final int expectedInvalidDataMessageCount = 5;
        final int expectedNoValidDataMessageCount = 10;

        var recepRow = ReferenceInterchangeRecep.fromString("RFF+RIS:00000005 OK:4");

        assertThat(recepRow.getInterchangeSequenceNumber())
            .isEqualTo(expectedReceivedInterchangeSequenceNumber);
        assertThat(recepRow.getRecepCode())
            .isEqualTo(ReferenceInterchangeRecep.RecepCode.RECEIVED);
        assertThat(recepRow.getMessageCount())
            .isEqualTo(expectedReceivedMessageCount);

        recepRow = ReferenceInterchangeRecep.fromString("RFF+RIS:10000006 ER:5:QWE+ASD");

        assertThat(recepRow.getInterchangeSequenceNumber())
            .isEqualTo(expectedInvalidDataInterchangeSequenceNumber);
        assertThat(recepRow.getRecepCode())
            .isEqualTo(ReferenceInterchangeRecep.RecepCode.INVALID_DATA);
        assertThat(recepRow.getMessageCount())
            .isEqualTo(expectedInvalidDataMessageCount);

        recepRow = ReferenceInterchangeRecep.fromString("RFF+RIS:99000006 NA:10:QWE:ASD++");

        assertThat(recepRow.getInterchangeSequenceNumber())
            .isEqualTo(expectedNoValidDataInterchangeSequenceNumber);
        assertThat(recepRow.getRecepCode())
            .isEqualTo(ReferenceInterchangeRecep.RecepCode.NO_VALID_DATA);
        assertThat(recepRow.getMessageCount())
            .isEqualTo(expectedNoValidDataMessageCount);
    }

    @Test
    void When_ParsingRecepCodeFromCode_Expect_RecepCodeIsCreated(SoftAssertions softly) {
        var toParse = new String[] {"OK", "NA", "ER"};

        for (int i = 0; i < ReferenceInterchangeRecep.RecepCode.values().length; i++) {
            var actual = ReferenceInterchangeRecep.RecepCode.fromCode(toParse[i]);
            var expected = ReferenceInterchangeRecep.RecepCode.values()[i];
            softly.assertThat(actual).isEqualTo(expected);
        }
    }
}
