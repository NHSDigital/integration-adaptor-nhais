package uk.nhs.digital.nhsconnect.nhais.translator;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.AmendmentNameToEdifactMapper;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentNameToEdifactTranslatorTest extends AmendmentFhirToEdifactTestBase {

    private static final String NHS_NUMBER = "1234";
    private static final String FAMILY_NAME = "Smith";
    private static final String FIRST_FORENAME = "John";
    private static final String SECOND_FORENAME = "Adam";
    private static final String OTHER_FORENAME = "Jacob";
    private static final String TITLE = "Mr";

    private final AmendmentNameToEdifactMapper translator = new AmendmentNameToEdifactMapper();

    @Mock
    private AmendmentBody amendmentBody;

    @Mock
    private JsonPatches jsonPatches;

    @BeforeEach
    void setUp() {
        reset(amendmentBody, jsonPatches);

        lenient().when(amendmentBody.getNhsNumber()).thenReturn(NHS_NUMBER);
        lenient().when(jsonPatches.getAmendmentBody()).thenReturn(amendmentBody);
        when(amendmentBody.getJsonPatches()).thenReturn(jsonPatches);
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddingOrReplacingAllFields_expectAllFieldsAreMapped(AmendmentPatchOperation operation) {
        when(jsonPatches.getSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(FAMILY_NAME))));
        when(jsonPatches.getFirstForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(FIRST_FORENAME))));
        when(jsonPatches.getSecondForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(SECOND_FORENAME))));
        when(jsonPatches.getOtherForenames()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(OTHER_FORENAME))));
        when(jsonPatches.getTitle()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(TITLE))));

        var segments = translator.map(amendmentBody);

        assertThat(segments).usingFieldByFieldElementComparator()
            .containsExactly(PersonName.builder()
                .nhsNumber(NHS_NUMBER)
                .patientIdentificationType(PersonName.PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
                .familyName(FAMILY_NAME)
                .forename(FIRST_FORENAME)
                .middleName(SECOND_FORENAME)
                .thirdForename(OTHER_FORENAME)
                .title(TITLE)
                .build());
    }

    @Test
    void whenRemovingAllFields_expectAllFieldsAreMapped() {
        when(jsonPatches.getAllForenamesPath()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));
        when(jsonPatches.getTitle()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        var segments = translator.map(amendmentBody);

        assertThat(segments).usingFieldByFieldElementComparator()
            .containsExactly(PersonName.builder()
                .nhsNumber(NHS_NUMBER)
                .patientIdentificationType(PersonName.PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION)
                .forename(REMOVE_INDICATOR)
                .title(REMOVE_INDICATOR)
                .build());
    }

    @Test
    void whenRemovingSurname_expectException() {
        when(jsonPatches.getSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(FhirValidationException.class)
            .hasMessage("Removing surnames is illegal");
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenRemovingAllForenamesAndModifyingAtTheSameTime_expectException(AmendmentPatchOperation operation, SoftAssertions softly) {
        Stream.<Supplier<Optional<AmendmentPatch>>>of(
            jsonPatches::getFirstForename,
            jsonPatches::getSecondForename,
            jsonPatches::getOtherForenames)
            .forEach(patchSupplier -> {
                reset(jsonPatches);
                when(patchSupplier.get()).thenReturn(Optional.of(new AmendmentPatch()
                    .setValue(AmendmentValue.from("some_value"))
                    .setOp(operation)));
                when(jsonPatches.getAllForenamesPath()).thenReturn(Optional.of(new AmendmentPatch()
                    .setOp(operation)));

                softly.assertThatThrownBy(() -> translator.map(amendmentBody))
                    .isInstanceOf(FhirValidationException.class)
                    .hasMessage("Illegal to modify forenames and remove all at the same time");
            });
    }

    @Test
    void whenRemovingAnyForename_expectException(SoftAssertions softly) {
        Stream.<Supplier<Optional<AmendmentPatch>>>of(
            jsonPatches::getFirstForename,
            jsonPatches::getSecondForename,
            jsonPatches::getOtherForenames)
            .forEach(patchSupplier -> {
                reset(jsonPatches);
                when(patchSupplier.get()).thenReturn(Optional.of(new AmendmentPatch()
                    .setOp(AmendmentPatchOperation.REMOVE)
                    .setPath("/some/json/path/")));

                softly.assertThatThrownBy(() -> translator.map(amendmentBody))
                    .isInstanceOf(FhirValidationException.class)
                    .hasMessage("Removing /some/json/path/ is illegal. Use /name/0/given to remove all forenames instead");
            });
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddOrReplaceValuesAreEmpty_expectException(AmendmentPatchOperation operation) {
        when(jsonPatches.getTitle()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath("/title/")
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(jsonPatches.getSurname()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath("/surname/")
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(jsonPatches.getFirstForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath("/first_forename/")
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(jsonPatches.getSecondForename()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath("/second_forename/")
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));
        when(jsonPatches.getOtherForenames()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath("/other_forename/")
            .setValue(AmendmentValue.from(StringUtils.EMPTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(FhirValidationException.class)
            .hasMessage("Invalid values for: [/title/, /surname/, /first_forename/, /second_forename/, /other_forename/]");
    }
}