package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonPreviousNameTest {

    private static final String EMPTY = "PNA+PER";
    private static final String PREVIOUS_SURNAME = "PNA+PER++++SU:PATTERSON";

    @Test
    public void whenMappingToEdifact_expectReturnCorrectString() throws EdifactValidationException {
        assertThat(PersonPreviousName.builder().previousFamilyName("PATTERSON").build().toEdifact())
            .isEqualTo(PREVIOUS_SURNAME + "'");
    }

    @Test
    public void whenMappingToEdifactFromEmptySegment_expectException() {
        assertThatThrownBy(() -> PersonPreviousName.builder().build().toEdifact())
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> PersonPreviousName.fromString(EMPTY))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testFromString() {
        assertThat(PersonPreviousName.fromString(PREVIOUS_SURNAME)).isEqualTo(
            PersonPreviousName.builder().previousFamilyName("PATTERSON").build());
    }
}