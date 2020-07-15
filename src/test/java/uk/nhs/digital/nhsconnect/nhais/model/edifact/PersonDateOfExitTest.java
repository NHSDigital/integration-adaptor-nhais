package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonDateOfExitTest {

    private static final LocalDate FIXED_TIME = LocalDate.of(1992, 01, 13);


    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var personDateOfExit = new PersonDateOfExit(FIXED_TIME);

        assertThat(personDateOfExit.toEdifact()).isEqualTo("DTM+958:19920113:102'");
    }

    @Test
    public void When_BuildingWithEmptyTimestamp_Then_NullPointerExceptionIsThrown() {
        assertThatThrownBy(() -> new PersonDateOfExit(null))
            .isExactlyInstanceOf(NullPointerException.class);
    }

}