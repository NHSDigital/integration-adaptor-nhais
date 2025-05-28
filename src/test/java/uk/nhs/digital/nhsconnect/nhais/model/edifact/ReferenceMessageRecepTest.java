package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class ReferenceMessageRecepTest {

    private static final long MESSAGE_SEQUENCE_NUMBER = 123L;

    @Test
    void When_GettingKey_Expect_ReturnsProperValue() {
        String key = new ReferenceMessageRecep(
            MESSAGE_SEQUENCE_NUMBER, ReferenceMessageRecep.RecepCode.ERROR)
            .getKey();

        assertThat(key).isEqualTo("RFF");
    }

    @Test
    void When_GettingValue_Expect_ReturnsProperValue() {
        String value = new ReferenceMessageRecep(
            MESSAGE_SEQUENCE_NUMBER, ReferenceMessageRecep.RecepCode.ERROR)
            .getValue();

        assertThat("MIS:00000123 CA").isEqualTo(value);
    }

    @Test
    void When_PreValidatedDataViolatesNullChecks_Expect_ThrowsException(SoftAssertions softly) {
        softly.assertThatThrownBy(
            () -> new ReferenceMessageRecep(null, ReferenceMessageRecep.RecepCode.ERROR)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute messageSequenceNumber is required");

        softly.assertThatThrownBy(
            () -> new ReferenceMessageRecep(MESSAGE_SEQUENCE_NUMBER, null)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute recepCode is required");
    }

    @Test
    void When_Parsing_Expect_RecepCreated(SoftAssertions softly) {
        final long expectedSuccessMessageSequenceNumber = 5L;
        final long expectedErrorMessageSequenceNumber = 10000006L;
        final long expectedIncompleteMessageSequenceNumber = 99000006L;

        var recepRow = ReferenceMessageRecep.fromString("RFF+MIS:00000005 CP");

        softly.assertThat(recepRow.getMessageSequenceNumber()).isEqualTo(expectedSuccessMessageSequenceNumber);
        softly.assertThat(recepRow.getRecepCode()).isEqualTo(ReferenceMessageRecep.RecepCode.SUCCESS);

        recepRow = ReferenceMessageRecep.fromString("RFF+MIS:10000006 CA:5:QWE+ASD");

        softly.assertThat(recepRow.getMessageSequenceNumber()).isEqualTo(expectedErrorMessageSequenceNumber);
        softly.assertThat(recepRow.getRecepCode()).isEqualTo(ReferenceMessageRecep.RecepCode.ERROR);

        recepRow = ReferenceMessageRecep.fromString("RFF+MIS:99000006 CI+ASD++");

        softly.assertThat(recepRow.getMessageSequenceNumber()).isEqualTo(expectedIncompleteMessageSequenceNumber);
        softly.assertThat(recepRow.getRecepCode()).isEqualTo(ReferenceMessageRecep.RecepCode.INCOMPLETE);
    }

    @Test
    void When_ParsingRecepCodeFromCode_Expect_RecepCodeIsCreated(SoftAssertions softly) {
        var toParse = new String[] {"CP", "CA", "CI"};

        for (int i = 0; i < ReferenceMessageRecep.RecepCode.values().length; i++) {
            var actual = ReferenceMessageRecep.RecepCode.fromCode(toParse[i]);
            var expected = ReferenceMessageRecep.RecepCode.values()[i];
            softly.assertThat(actual).isEqualTo(expected);
        }
    }
}
