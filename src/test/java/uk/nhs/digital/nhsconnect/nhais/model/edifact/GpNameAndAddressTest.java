package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertSoftly(softly -> {
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

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "NAD+GP+4826940,281:900'";

        var personGP = GpNameAndAddress.builder()
            .identifier("4826940,281")
            .code("900")
            .build();

        assertEquals(expectedValue, personGP.toEdifact());
    }

    @Test
    public void When_MappingToEdifactWithEmptyMandatoryFields_Then_EdifactValidationExceptionIsThrown() {
        var personGP = GpNameAndAddress.builder()
            .identifier("")
            .code("")
            .build();

        assertThrows(EdifactValidationException.class, personGP::toEdifact);
    }

    @Test
    public void When_BuildingWithoutMandatoryFields_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> GpNameAndAddress.builder().build());
    }
}