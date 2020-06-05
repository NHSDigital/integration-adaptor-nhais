package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GpNameAndAddressTest {

    public final GpNameAndAddress gpNameAndAddress = new GpNameAndAddress("ABC", "code1");

    @Test
    void testGetKey() {
        assertThat(gpNameAndAddress.getKey()).isEqualTo("NAD");
    }

    @Test
    void testGetValue() {
        assertThat(gpNameAndAddress.getValue()).isEqualTo("GP+ABC:code1");
    }

    @Test
    void testValidateStateful() {
        assertThatCode(gpNameAndAddress::validateStateful).doesNotThrowAnyException();
    }

    @Test
    void testPreValidate() {
        GpNameAndAddress emptyIdentifier = new GpNameAndAddress("", "x");
        GpNameAndAddress emptyCode = new GpNameAndAddress("x", "");
        SoftAssertions.assertSoftly( softly -> {
            softly.assertThatThrownBy(emptyIdentifier::preValidate)
                .isExactlyInstanceOf(EdifactValidationException.class)
                .hasMessage("NAD: Attribute identifier is required");

            softly.assertThatThrownBy(emptyCode::preValidate)
                .isExactlyInstanceOf(EdifactValidationException.class)
                .hasMessage("NAD: Attribute code is required");
        });
    }

    @Test
    void testFromString() {
        assertThat(GpNameAndAddress.fromString("NAD+GP+ABC:code1").getValue()).isEqualTo(gpNameAndAddress.getValue());
        assertThatThrownBy(() -> GpNameAndAddress.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}