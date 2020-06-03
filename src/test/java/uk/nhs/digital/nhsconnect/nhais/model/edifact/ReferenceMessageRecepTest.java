package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class ReferenceMessageRecepTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void whenGettingKey_thenReturnsProperValue() {
        String key = new ReferenceMessageRecep(
            123L, ReferenceMessageRecep.RecepCode.ERROR)
            .getKey();

        assertThat(key).isEqualTo("RFF");
    }

    @Test
    void whenGettingValue_thenReturnsProperValue() {
        String value = new ReferenceMessageRecep(
            123L, ReferenceMessageRecep.RecepCode.ERROR)
            .getValue();

        assertThat(value).isEqualTo("MIS:123 CA");
    }

    @Test
    void whenPreValidatedDataViolatesNullChecks_thenThrowsException(SoftAssertions softly) {
        softly.assertThatThrownBy(
            () -> new ReferenceMessageRecep(null, ReferenceMessageRecep.RecepCode.ERROR)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute messageSequenceNumber is required");

        softly.assertThatThrownBy(
            () -> new ReferenceMessageRecep(123L, null)
                .preValidate())
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute recepCode is required");
    }
}
