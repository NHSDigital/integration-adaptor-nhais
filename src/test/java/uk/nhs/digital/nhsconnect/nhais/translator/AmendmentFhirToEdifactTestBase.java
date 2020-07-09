package uk.nhs.digital.nhsconnect.nhais.translator;

import org.junit.jupiter.params.provider.Arguments;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;

import java.util.stream.Stream;

public class AmendmentFhirToEdifactTestBase {
    protected static final String REMOVE_INDICATOR = "%";

    protected static Stream<Arguments> getAddOrReplaceEnums() {
        return Stream.of(
            AmendmentPatchOperation.ADD,
            AmendmentPatchOperation.REPLACE)
            .map(Arguments::of);
    }

}
