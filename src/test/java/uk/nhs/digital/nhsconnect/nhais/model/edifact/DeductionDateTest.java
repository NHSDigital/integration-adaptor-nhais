package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DeductionDateTest {

    @Test
    public void When_deductionDateToEdifact_Then_edifactFormattedCorrectly() {
        DeductionDate deductionDate = new DeductionDate(LocalDate.parse("2005-01-15"));
        assertThat(deductionDate.toEdifact()).isEqualTo("DTM+961:20050115:102'");
    }

    @Test
    void When_fromStringWithValidInput_Then_SegmentCreated() {
        DeductionDate deductionDate = DeductionDate.fromString("DTM+961:20050115:102");
        DeductionDate expectedDeductionDate = new DeductionDate(LocalDate.of(2005,1,15));

        assertThat(deductionDate.getValue()).isEqualTo(expectedDeductionDate.getValue());
    }
    @Test
    void When_fromStringWithInvalidInput_Then_ExceptionThrown() {
        assertThatThrownBy(() -> DeductionDate.fromString("DTM+96999:20050115:102"))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}