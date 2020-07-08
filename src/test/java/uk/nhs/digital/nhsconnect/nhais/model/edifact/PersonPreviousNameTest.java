package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class PersonPreviousNameTest {

    private static final String EMPTY = "PNA+PER";
    private static final String PREVIOUS_SURNAME = "PNA+PER++++SU:PATTERSON";

    @Test
    public void whenMappingToEdifact_expectReturnCorrectString() throws EdifactValidationException {
        assertThat(PersonPreviousName.builder().previousFamilyName("PATTERSON").build().toEdifact())
            .isEqualTo(PREVIOUS_SURNAME + "'");

        assertThat(PersonPreviousName.builder().build().toEdifact())
            .isEqualTo(EMPTY + "'");
    }

    @Test
    void testFromString() {
        assertThat(PersonPreviousName.fromString(PREVIOUS_SURNAME)).isEqualTo(
            PersonPreviousName.builder().previousFamilyName("PATTERSON").build());

        assertThat(PersonPreviousName.fromString(EMPTY)).isEqualTo(
            PersonPreviousName.builder().build());
    }
}