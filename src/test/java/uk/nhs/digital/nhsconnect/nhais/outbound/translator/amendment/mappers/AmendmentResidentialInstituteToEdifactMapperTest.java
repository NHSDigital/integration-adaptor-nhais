package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.outbound.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ResidentialInstituteNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentStringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentResidentialInstituteToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {
    @InjectMocks
    private AmendmentResidentialInstituteToEdifactMapper translator;

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddingOrReplacingWithCorrectValue_expectFieldsAreMapped(AmendmentPatchOperation operation) {
        when(jsonPatches.getResidentialInstituteCode()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setValue(new AmendmentStringExtension.ResidentialInstituteCode("null"))));

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(new ResidentialInstituteNameAndAddress("null"));
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenRemoving_expectFieldsAreRemoved(AmendmentPatchOperation operation) {
        when(jsonPatches.getResidentialInstituteCode()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setValue(new AmendmentStringExtension.ResidentialInstituteCode(null))));

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(new ResidentialInstituteNameAndAddress("%"));
    }

    @Test
    void whenUsingRemoveOperation_expectException() {
        when(jsonPatches.getResidentialInstituteCode()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Removing Residential Institute Code should be done using extension with 'null' value");
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddOrReplaceValuesAreEmpty_expectException(AmendmentPatchOperation operation) {
        when(jsonPatches.getResidentialInstituteCode()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setValue(new AmendmentStringExtension.ResidentialInstituteCode(StringUtils.EMPTY))
        ));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("String value must not be empty");
    }
}