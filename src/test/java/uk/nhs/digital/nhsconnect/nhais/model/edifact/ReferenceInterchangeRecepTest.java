package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class ReferenceInterchangeRecepTest {
    @Test
    void whenGettingKey_thenReturnsProperValue() {
        String key = new ReferenceInterchangeRecep(
            123L, ReferenceInterchangeRecep.RecepCode.RECEIVED, 3)
            .getKey();

        assertThat(key).isEqualTo("RFF");
    }

    @Test
    void whenGettingValue_thenReturnsProperValue() {
        String value = new ReferenceInterchangeRecep(
            123L, ReferenceInterchangeRecep.RecepCode.RECEIVED, 3)
            .getValue();

        assertThat("RIS:00000123 OK:3").isEqualTo(value);
    }

    @Test
    void whenPreValidatedDataViolatesNullChecks_thenThrowsException(SoftAssertions softly) {
        softly.assertThatThrownBy(
            () -> new ReferenceInterchangeRecep(null, ReferenceInterchangeRecep.RecepCode.RECEIVED, 3)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute messageSequenceNumber is required");

        softly.assertThatThrownBy(
            () -> new ReferenceInterchangeRecep(123L, null, 3)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute recepCode is required");

        softly.assertThatThrownBy(
            () -> new ReferenceInterchangeRecep(123L, ReferenceInterchangeRecep.RecepCode.RECEIVED, null)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute messageCount is required");
    }

    @Test
    void whenParsing_thenRecepCreated() {
        var recepRow = ReferenceInterchangeRecep.fromString("RFF+RIS:00000005 OK:4");

        assertThat(recepRow.getInterchangeSequenceNumber()).isEqualTo(5L);
        assertThat(recepRow.getRecepCode()).isEqualTo(ReferenceInterchangeRecep.RecepCode.RECEIVED);
        assertThat(recepRow.getMessageCount()).isEqualTo(4);

        recepRow = ReferenceInterchangeRecep.fromString("RFF+RIS:10000006 ER:5:QWE+ASD");

        assertThat(recepRow.getInterchangeSequenceNumber()).isEqualTo(10000006L);
        assertThat(recepRow.getRecepCode()).isEqualTo(ReferenceInterchangeRecep.RecepCode.INVALID_DATA);
        assertThat(recepRow.getMessageCount()).isEqualTo(5);

        recepRow = ReferenceInterchangeRecep.fromString("RFF+RIS:99000006 NA:10:QWE:ASD++");

        assertThat(recepRow.getInterchangeSequenceNumber()).isEqualTo(99000006L);
        assertThat(recepRow.getRecepCode()).isEqualTo(ReferenceInterchangeRecep.RecepCode.NO_VALID_DATA);
        assertThat(recepRow.getMessageCount()).isEqualTo(10);
    }

    @Test
    void whenParsingRecepCodeFromCode_thenRecepCodeIsCreated(SoftAssertions softly) {
        var toParse = new String[] {"OK", "NA", "ER"};

        for (int i = 0; i < ReferenceInterchangeRecep.RecepCode.values().length; i++) {
            var actual = ReferenceInterchangeRecep.RecepCode.fromCode(toParse[i]);
            var expected = ReferenceInterchangeRecep.RecepCode.values()[i];
            softly.assertThat(actual).isEqualTo(expected);
        }
    }
}
