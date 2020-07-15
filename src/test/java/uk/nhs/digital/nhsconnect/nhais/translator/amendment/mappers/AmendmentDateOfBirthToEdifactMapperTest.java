package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AmendmentDateOfBirthToEdifactMapperTest extends AmendmentFhirToEdifactTestBase {

    private static final String DATE_OF_BIRTH = "1990-01-02";
    private static final Instant DATE_OF_BIRTH_TIMESTAMP = Instant.ofEpochMilli(1234);

    @InjectMocks
    private AmendmentDateOfBirthToEdifactMapper translator;

    @Mock
    private TimestampService timestampService;

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddingOrReplacingDateOfBirth_expectFieldsAreMapped(AmendmentPatchOperation operation) {
        when(jsonPatches.getBirthDate()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(DATE_OF_BIRTH))));
        when(timestampService.parseDate(DATE_OF_BIRTH)).thenReturn(DATE_OF_BIRTH_TIMESTAMP);

        var segments = translator.map(amendmentBody);

        assertThat(segments).isPresent().get()
            .isEqualTo(PersonDateOfBirth.builder()
                .timestamp(DATE_OF_BIRTH_TIMESTAMP)
                .build());
    }

    @Test
    void whenUsingRemoveOperation_expectException() {
        when(jsonPatches.getBirthDate()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REMOVE)));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Illegal remove operation on /birthDate");
    }

    @ParameterizedTest
    @MethodSource(value = "getAddOrReplaceEnums")
    void whenAddOrReplaceValuesAreEmpty_expectException(AmendmentPatchOperation operation) {
        when(jsonPatches.getBirthDate()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation)
            .setPath(JsonPatches.BIRTH_DATE_PATH)
            .setValue(AmendmentValue.from(StringUtils.EMPTY))
        ));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isInstanceOf(PatchValidationException.class)
            .hasMessage("Invalid values for: [/birthDate]");
    }
}
