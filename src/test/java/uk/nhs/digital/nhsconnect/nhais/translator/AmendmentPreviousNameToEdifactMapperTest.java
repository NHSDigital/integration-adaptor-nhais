package uk.nhs.digital.nhsconnect.nhais.translator;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.AmendmentPreviousNameToEdifactMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentPreviousNameToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {

    private static final String PREVIOUS_SURNAME = "Snow";

    private final AmendmentPreviousNameToEdifactMapper translator = new AmendmentPreviousNameToEdifactMapper();

    @Mock
    private AmendmentBody amendmentBody;

    @Mock
    private JsonPatches jsonPatches;

    @BeforeEach
    void setUp() {
        reset(amendmentBody, jsonPatches);

        when(amendmentBody.getJsonPatches()).thenReturn(jsonPatches);
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddingOrReplacingPreviousSurname_expectAllFieldsAreMapped(AmendmentPatchOperation operation) {
        when(jsonPatches.getPreviousSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(PREVIOUS_SURNAME))));

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(PersonPreviousName.builder()
                .previousFamilyName(PREVIOUS_SURNAME)
                .build());
    }

    @Test
    void whenRemovingPreviousSurname_expectAllFieldsAreMapped() {
        when(jsonPatches.getPreviousSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(PersonPreviousName.builder()
                .previousFamilyName(REMOVE_INDICATOR)
                .build());
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddOrReplaceValuesAreEmpty_expectException(AmendmentPatchOperation operation) {
        when(jsonPatches.getPreviousSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath("/previous_surname/")
            .setValue(AmendmentValue.from(StringUtils.EMPTY))
        ));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(FhirValidationException.class)
            .hasMessage("Invalid values for: [/previous_surname/]");
    }
}