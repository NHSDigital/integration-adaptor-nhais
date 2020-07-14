package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonSexTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "PDI+1'";

        var personSex = PersonSex.builder()
            .gender(PersonSex.Gender.MALE)
            .build();

        assertEquals(expectedValue, personSex.toEdifact());
    }

    @Test
    public void When_BuildingWithoutType_Then_NullPointerExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> PersonSex.builder().build());
    }
}
