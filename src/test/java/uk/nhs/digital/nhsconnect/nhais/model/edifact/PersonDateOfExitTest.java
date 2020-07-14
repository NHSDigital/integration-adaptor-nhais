package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonDateOfExitTest {

    private static final Instant FIXED_TIME = ZonedDateTime.of(
        1992,
        1,
        13,
        23,
        55,
        0,
        0,
        ZoneId.of("Europe/London")).toInstant();


    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var personDateOfExit = PersonDateOfExit.builder()
            .timestamp(FIXED_TIME)
            .build();

        assertThat(personDateOfExit.toEdifact()).isEqualTo("DTM+958:19920113:102'");
    }

    @Test
    public void When_BuildingWithEmptyTimestamp_Then_NullPointerExceptionIsThrown() {
        assertThatThrownBy(() -> PersonDateOfExit.builder().build())
            .isExactlyInstanceOf(NullPointerException.class);
    }

}