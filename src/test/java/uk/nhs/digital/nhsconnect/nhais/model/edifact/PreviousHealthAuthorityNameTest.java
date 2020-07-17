package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PreviousHealthAuthorityNameTest {

    private final static String IDENTIFIER = "ID1";

    @Test
    void toEdifact() {
        PreviousHealthAuthorityName previousHealthAuthorityName = new PreviousHealthAuthorityName(IDENTIFIER);
        assertThat(previousHealthAuthorityName.toEdifact()).isEqualTo("NAD+PFH+ID1:954'");
    }

    @Test
    void getKey() {
        PreviousHealthAuthorityName previousHealthAuthorityName = new PreviousHealthAuthorityName(IDENTIFIER);
        assertThat(previousHealthAuthorityName.getKey()).isEqualTo("NAD");
    }

    @Test
    void getValue() {
        PreviousHealthAuthorityName previousHealthAuthorityName = new PreviousHealthAuthorityName(IDENTIFIER);
        assertThat(previousHealthAuthorityName.getValue()).isEqualTo("PFH+ID1:954");
    }

    @Test
    void preValidate() {
        PreviousHealthAuthorityName previousHealthAuthorityName = new PreviousHealthAuthorityName(StringUtils.EMPTY);
        assertThatCode(previousHealthAuthorityName::preValidate)
            .isExactlyInstanceOf(EdifactValidationException.class);
    }
}