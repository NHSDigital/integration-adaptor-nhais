package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.outbound.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentificationType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentNameToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {

    private static final String NHS_NUMBER = "1234";
    private static final String SURNAME = "Smith";
    private static final String FIRST_FORENAME = "John";
    private static final String SECOND_FORENAME = "Adam";
    private static final String OTHER_FORENAMES = "Jacob";
    private static final String TITLE = "Mr";

    private final AmendmentNameToEdifactMapper translator = new AmendmentNameToEdifactMapper();


    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        lenient().when(super.getAmendmentBody().getNhsNumber()).thenReturn(NHS_NUMBER);
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void When_AddingOrReplacingAllFields_Expect_AllFieldsAreMapped(AmendmentPatchOperation operation) {
        when(super.getJsonPatches().getSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(SURNAME))));
        when(super.getJsonPatches().getFirstForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(FIRST_FORENAME))));
        when(super.getJsonPatches().getSecondForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(SECOND_FORENAME))));
        when(super.getJsonPatches().getOtherForenames()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(OTHER_FORENAMES))));
        when(super.getJsonPatches().getTitle()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(TITLE))));

        var segments = translator.map(super.getAmendmentBody());

        assertThat(segments).isNotEmpty().get()
            .isEqualTo(PersonName.builder()
                .nhsNumber(NHS_NUMBER)
                .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
                .surname(SURNAME)
                .firstForename(FIRST_FORENAME)
                .secondForename(SECOND_FORENAME)
                .otherForenames(OTHER_FORENAMES)
                .title(TITLE)
                .build());
    }

    @Test
    void When_RemovingAllFields_Expect_AllFieldsAreMapped() {
        when(super.getJsonPatches().getAllForenamesPath()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));
        when(super.getJsonPatches().getTitle()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        var segments = translator.map(super.getAmendmentBody());

        assertThat(segments).isNotEmpty().get()
            .isEqualTo(PersonName.builder()
                .nhsNumber(NHS_NUMBER)
                .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
                .firstForename(REMOVE_INDICATOR)
                .title(REMOVE_INDICATOR)
                .build());
    }

    @Test
    void When_RemovingSurname_Expect_Exception() {
        when(super.getJsonPatches().getSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Removing surnames is illegal");
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void When_RemovingAllForenamesAndModifyingAtTheSameTime_Expect_Exception(AmendmentPatchOperation operation, SoftAssertions softly) {
        Stream.<Supplier<Optional<AmendmentPatch>>>of(
            super.getJsonPatches()::getFirstForename,
            super.getJsonPatches()::getSecondForename,
            super.getJsonPatches()::getOtherForenames)
            .forEach(patchSupplier -> {
                reset(super.getJsonPatches());
                when(patchSupplier.get()).thenReturn(Optional.of(new AmendmentPatch()
                    .setValue(AmendmentValue.from("some_value"))
                    .setOp(operation)));
                when(super.getJsonPatches().getAllForenamesPath()).thenReturn(Optional.of(new AmendmentPatch()
                    .setOp(operation)));

                softly.assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
                    .isInstanceOf(PatchValidationException.class)
                    .hasMessage("Illegal to modify forenames and remove all at the same time");
            });
    }

    @Test
    void When_RemovingAnyForename_Expect_Exception(SoftAssertions softly) {
        reset(super.getJsonPatches());
        when(super.getJsonPatches().getFirstForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)
            .setPath(JsonPatches.FIRST_FORENAME_PATH)));
        softly.assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Removing /name/0/given/0 is illegal. Use /name/0/given to remove all forenames instead");

        reset(super.getJsonPatches());
        when(super.getJsonPatches().getSecondForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)
            .setPath(JsonPatches.SECOND_FORENAME_PATH)));
        softly.assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Removing /name/0/given/1 is illegal. Use /name/0/given to remove all forenames instead");

        reset(super.getJsonPatches());
        when(super.getJsonPatches().getOtherForenames()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)
            .setPath(JsonPatches.OTHER_FORENAMES_PATH)));
        softly.assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Removing /name/0/given/2 is illegal. Use /name/0/given to remove all forenames instead");
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void When_AddOrReplaceValuesAreEmpty_Expect_Exception(AmendmentPatchOperation operation) {
        when(super.getJsonPatches().getTitle()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath(JsonPatches.TITLE_PATH)
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(super.getJsonPatches().getSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath(JsonPatches.SURNAME_PATH)
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(super.getJsonPatches().getFirstForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath(JsonPatches.FIRST_FORENAME_PATH)
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(super.getJsonPatches().getSecondForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath(JsonPatches.SECOND_FORENAME_PATH)
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(super.getJsonPatches().getOtherForenames()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath(JsonPatches.OTHER_FORENAMES_PATH)
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));

        assertThatThrownBy(() -> translator.map(super.getAmendmentBody()))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Invalid values for: [/name/0/prefix/0, /name/0/family, /name/0/given/0, /name/0/given/1, /name/0/given/2]");
    }
}