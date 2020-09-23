package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptanceDateTest {

    private final LocalDate DATE = LocalDate.of(2020, 3, 28);

    @Test
    public void When_toEdifact_And_instantInWinter_Then_edifactIsCorrect() throws EdifactValidationException {
        assertThat(new AcceptanceDate(DATE).toEdifact()).isEqualTo("DTM+956:20200328:102'");
    }

}