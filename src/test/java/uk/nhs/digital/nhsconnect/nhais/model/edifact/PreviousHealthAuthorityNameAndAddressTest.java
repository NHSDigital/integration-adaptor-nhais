package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PreviousHealthAuthorityNameAndAddressTest {

    private final static String IDENTIFIER = "ID1";

    @Test
    void toEdifact() {
        PreviousHealthAuthorityNameAndAddress previousHealthAuthorityNameAndAddress = new PreviousHealthAuthorityNameAndAddress(IDENTIFIER);
        assertThat(previousHealthAuthorityNameAndAddress.toEdifact()).isEqualTo("NAD+PFH+ID1:954'");
    }

    @Test
    void getKey() {
        PreviousHealthAuthorityNameAndAddress previousHealthAuthorityNameAndAddress = new PreviousHealthAuthorityNameAndAddress(IDENTIFIER);
        assertThat(previousHealthAuthorityNameAndAddress.getKey()).isEqualTo("NAD");
    }

    @Test
    void getValue() {
        PreviousHealthAuthorityNameAndAddress previousHealthAuthorityNameAndAddress = new PreviousHealthAuthorityNameAndAddress(IDENTIFIER);
        assertThat(previousHealthAuthorityNameAndAddress.getValue()).isEqualTo("PFH+ID1:954");
    }

    @Test
    void preValidate() {
        PreviousHealthAuthorityNameAndAddress previousHealthAuthorityNameAndAddress = new PreviousHealthAuthorityNameAndAddress(StringUtils.EMPTY);
        assertThatCode(previousHealthAuthorityNameAndAddress::preValidate)
            .isExactlyInstanceOf(EdifactValidationException.class);
    }
}