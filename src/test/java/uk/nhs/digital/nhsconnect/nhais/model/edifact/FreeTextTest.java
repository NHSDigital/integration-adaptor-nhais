package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FreeTextTest {
    @Test
    void toEdifactTest() {
        var edifact = new FreeText("Something").toEdifact();

        assertThat(edifact).isEqualTo("FTX+RGI+++Something'");
    }

    @Test
    void testFromString() {
        var edifact = "FTX+RGI+++WRONG HA - TRY SURREY'";
        var parsedFreeText = FreeText.fromString("FTX+RGI+++WRONG HA - TRY SURREY");
        assertThat(parsedFreeText.getTextLiteral()).isEqualTo("WRONG HA - TRY SURREY");
        assertThat(parsedFreeText.toEdifact()).isEqualTo(edifact);
        assertThatThrownBy(() -> FreeText.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testPreValidationEmptyString() {
        FreeText emptyFreeText = new FreeText(StringUtils.EMPTY);
        assertThatThrownBy(emptyFreeText::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("FTX: Attribute textLiteral is required");
    }

    @Test
    public void testPreValidationBlankString() {
        FreeText emptyFreeText = new FreeText(" ");
        assertThatThrownBy(emptyFreeText::preValidate)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("FTX: Attribute textLiteral is required");
    }
}
