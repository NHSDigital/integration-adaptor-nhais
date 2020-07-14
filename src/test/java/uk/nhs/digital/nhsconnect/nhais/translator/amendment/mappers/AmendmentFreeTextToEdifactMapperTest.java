package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentFreeTextToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {
    private static final String FREE_TEXT = "qwe";

    @InjectMocks
    private AmendmentFreeTextToEdifactMapper translator;

    @Test
    void whenFreeTextIsPresent_expectValueIsMapped() {
        when(amendmentBody.getFreeText()).thenReturn(FREE_TEXT);

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(new FreeText(FREE_TEXT));
    }

    @Test
    void whenFreeTextIsEmpty_expectValueIsMapped() {
        when(amendmentBody.getFreeText()).thenReturn(StringUtils.EMPTY);

        var segments = translator.map(amendmentBody);

        assertThat(segments).isEmpty();
    }
}
