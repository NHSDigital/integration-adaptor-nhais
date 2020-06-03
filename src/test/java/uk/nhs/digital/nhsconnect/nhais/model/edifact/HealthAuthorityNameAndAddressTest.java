package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HealthAuthorityNameAndAddressTest {

    public final HealthAuthorityNameAndAddress healthAuthorityNameAndAddress = new HealthAuthorityNameAndAddress("ABC", "code1");

    @Test
    void testGetKey() {
        assertThat(healthAuthorityNameAndAddress.getKey()).isEqualTo("NAD");
    }

    @Test
    void testGetValue() {
        assertThat(healthAuthorityNameAndAddress.getValue()).isEqualTo("FHS+ABC:code1");
    }

    @Test
    void testValidateStateful() {
        assertThatCode(healthAuthorityNameAndAddress::validateStateful).doesNotThrowAnyException();
    }

    @Test
    void testPreValidate() {
        HealthAuthorityNameAndAddress emptyIdentifier = new HealthAuthorityNameAndAddress("", "x");
        HealthAuthorityNameAndAddress emptyCode = new HealthAuthorityNameAndAddress("x", "");
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
        assertThat(HealthAuthorityNameAndAddress.fromString("NAD+FHS+ABC:code1").getValue()).isEqualTo(healthAuthorityNameAndAddress.getValue());
        assertThatThrownBy(() -> HealthAuthorityNameAndAddress.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}