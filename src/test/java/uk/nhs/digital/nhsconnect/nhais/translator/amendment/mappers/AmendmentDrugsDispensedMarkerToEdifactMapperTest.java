package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBooleanExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentDrugsDispensedMarkerToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {
    @InjectMocks
    private AmendmentDrugsDispensedMarkerToEdifactMapper translator;

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddingOrReplacingWithCorrectValue_expectFieldsAreMapped(AmendmentPatchOperation operation) {
        when(jsonPatches.getDrugsDispensedMarker()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setValue(new AmendmentBooleanExtension.DrugsDispensedMarker(true))));

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(new DrugsMarker(true));
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenRemoving_expectFieldsAreRemoved(AmendmentPatchOperation operation) {
        when(jsonPatches.getDrugsDispensedMarker()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setValue(new AmendmentBooleanExtension.DrugsDispensedMarker(false))));

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(new DrugsMarker(false));
    }

    @Test
    void whenUsingRemoveOperation_expectException() {
        when(jsonPatches.getDrugsDispensedMarker()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Removing Drugs Dispensed Marker should be done using extension with 'false' value");
    }
}
