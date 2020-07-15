package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentFreeTextToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {
    private static final String FREE_TEXT = "qwe";

    @InjectMocks
    private AmendmentFreeTextToEdifactMapper translator;

    protected static Stream<Arguments> getEmptyValues() {
        return Stream.of(StringUtils.EMPTY, null)
            .map(Arguments::of);
    }

    @Test
    void whenFreeTextIsPresent_expectValueIsMapped() {
        when(amendmentBody.getFreeText()).thenReturn(FREE_TEXT);

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(new FreeText(FREE_TEXT));
    }

    @ParameterizedTest
    @MethodSource(value = "getEmptyValues")
    void whenFreeTextIsEmpty_expectValueIsMapped(String value) {
        when(amendmentBody.getFreeText()).thenReturn(value);

        var segments = translator.map(amendmentBody);

        assertThat(segments).isEmpty();
    }
}
