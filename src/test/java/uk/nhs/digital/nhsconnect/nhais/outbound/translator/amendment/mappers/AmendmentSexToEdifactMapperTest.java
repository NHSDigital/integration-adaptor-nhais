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
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentSexToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {

    @InjectMocks
    private AmendmentSexToEdifactMapper translator;

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void When_AddingOrReplacingWithCorrectValue_Expect_FieldsAreMapped(AmendmentPatchOperation operation) {
        when(super.getJsonPatches().getSex()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from("female"))));

        var segments = translator.map(super.getAmendmentBody());

        assertThat(segments).isPresent().get()
            .isEqualTo(PersonSex.builder()
                .gender(PersonSex.Gender.FEMALE)
                .build());
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void When_AddingOrReplacingWithIncorrectValue_Expect_Exception(AmendmentPatchOperation operation) {
        when(super.getJsonPatches().getSex()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from("qwe"))));

        assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No gender value for 'qwe'");
    }

    @Test
    void When_UsingRemoveOperation_Expect_Exception() {
        when(super.getJsonPatches().getSex()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Illegal remove operation on /gender");
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void When_AddOrReplaceValuesAreEmpty_Expect_Exception(AmendmentPatchOperation operation) {
        when(super.getJsonPatches().getSex()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath(JsonPatches.SEX_PATH)
            .setValue(AmendmentValue.from(StringUtils.EMPTY))
        ));

        assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Invalid values for: [/gender]");
    }
}
