package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonSexTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "PDI+1'";

        var personSex = PersonSex.builder()
            .sexCode("1")
            .build();

        assertEquals(expectedValue, personSex.toEdifact());
    }

    @Test
    public void When_MappingWithWrongCode_Then_EdifactValidationExceptionIsThrown() {
        var personSex = PersonSex.builder()
            .sexCode("abc")
            .build();

        assertThrows(EdifactValidationException.class, personSex::toEdifact);
    }

    @Test
    public void When_MappingToEdifactWithEmptySexCode_Then_EdifactValidationExceptionIsThrown() {
        var personSex = PersonSex.builder()
            .sexCode("")
            .build();

        assertThrows(EdifactValidationException.class, personSex::toEdifact);
    }

    @Test
    public void When_BuildingWithoutType_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonSex.builder().build());
    }
}
