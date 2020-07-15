package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PersonDateOfEntryTest {
    private static final LocalDate LOCAL_DATE = LocalDate.parse("1991-01-13");


    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var personDateOfEntry = new PersonDateOfEntry(LOCAL_DATE);
        assertThat(personDateOfEntry.toEdifact()).isEqualTo("DTM+957:19910113:102'");
    }

    @Test
    public void When_BuildingWithEmptyTimestamp_Then_NullPointerExceptionIsThrown() {
        assertThatThrownBy(() -> new PersonDateOfEntry(null))
            .isExactlyInstanceOf(NullPointerException.class);
    }
}
