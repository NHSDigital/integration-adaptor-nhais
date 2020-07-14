package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;

import static org.assertj.core.api.Assertions.assertThat;

class FreeTextMapperTest {

    private final FreeTextMapper freeTextMapper = new FreeTextMapper();

    @Test
    void when_FreeTextValueExistsAndValueIsSet_Then_CanMap() {
        Parameters parameters = new Parameters()
            .addParameter("freeText", "text");

        assertThat(freeTextMapper.canMap(parameters)).isTrue();
    }

    @Test
    void when_FreeTextValueExistsAndValueIsNotSet_Then_CanMap() {
        Parameters parameters = new Parameters()
            .addParameter("freeText", "");

        assertThat(freeTextMapper.canMap(parameters)).isFalse();
    }

    @Test
    void when_FreeTextValueDoesntExist_Then_CantMap() {
        Parameters parameters = new Parameters();

        assertThat(freeTextMapper.canMap(parameters)).isFalse();
    }

    @Test
    void when_DrugsMarkerExtensionExistsAndValueIsSet_Then_MappingSuccessful() {
        Parameters parameters = new Parameters()
            .addParameter("freeText", "text");

        FreeText freeText = freeTextMapper.map(parameters);

        FreeText expected = new FreeText("text");

        assertThat(freeText.toEdifact()).isEqualTo(expected.toEdifact());
        assertThat(freeText.toEdifact()).isEqualTo("FTX+RGI+++text'");
    }

}