package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.stream.Stream;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class AmendmentFhirToEdifactTestBase {
    protected static final String REMOVE_INDICATOR = "%";

    @Mock
    AmendmentBody amendmentBody;

    @Mock
    JsonPatches jsonPatches;

    @SuppressWarnings("unused")
    protected static Stream<Arguments> getAddOrReplaceEnums() {
        return Stream.of(
            AmendmentPatchOperation.ADD,
            AmendmentPatchOperation.REPLACE)
            .map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        reset(amendmentBody, jsonPatches);
        when(amendmentBody.getJsonPatches()).thenReturn(jsonPatches);
    }

}
