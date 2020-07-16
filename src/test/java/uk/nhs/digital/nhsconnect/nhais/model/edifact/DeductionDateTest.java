package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DeductionDateTest {

    @Test
    public void When_deductionDateToEdifact_Then_edifactFormattedCorrectly() {
        DeductionDate deductionDate = new DeductionDate(LocalDate.parse("2005-01-15"));
        assertThat(deductionDate.toEdifact()).isEqualTo("DTM+961:20050115:102'");
    }
}