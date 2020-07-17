package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NewHealthAuthorityNameTest {

    private final static String IDENTIFIER = "ID1";

    @Test
    void toEdifact() {
        NewHealthAuthorityName newHealthAuthorityName = new NewHealthAuthorityName(IDENTIFIER);
        assertThat(newHealthAuthorityName.toEdifact()).isEqualTo("NAD+NFH+ID1:954'");
    }

    @Test
    void getKey() {
        NewHealthAuthorityName newHealthAuthorityName = new NewHealthAuthorityName(IDENTIFIER);
        assertThat(newHealthAuthorityName.getKey()).isEqualTo("NAD");
    }

    @Test
    void getValue() {
        NewHealthAuthorityName newHealthAuthorityName = new NewHealthAuthorityName(IDENTIFIER);
        assertThat(newHealthAuthorityName.getValue()).isEqualTo("NFH+ID1:954");
    }

    @Test
    void preValidate() {
        NewHealthAuthorityName newHealthAuthorityName = new NewHealthAuthorityName(StringUtils.EMPTY);
        assertThatCode(newHealthAuthorityName::preValidate)
            .isExactlyInstanceOf(EdifactValidationException.class);
    }

    @Test
    void When_fromStringWithValidInput_Then_SegmentCreated() {
        NewHealthAuthorityName newHealthAuthorityName = NewHealthAuthorityName.fromString("NAD+NFH+ID1:954");
        NewHealthAuthorityName expectedNewHealthAuthorityName = new NewHealthAuthorityName(IDENTIFIER);

        assertThat(newHealthAuthorityName.getValue()).isEqualTo(expectedNewHealthAuthorityName.getValue());
    }
    @Test
    void When_fromStringWithInvalidInput_Then_ExceptionThrown() {
        assertThatThrownBy(() -> DeductionDate.fromString("DTM+96999:20050115:102"))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

}