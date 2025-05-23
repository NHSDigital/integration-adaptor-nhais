package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FP69ExpiryDateTest {

    @Test
    void When_SettingNullTimestamp_Expect_Exception() {
        assertThatThrownBy(() -> new FP69ExpiryDate(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void toEdifact() {
        var fp69ReasonCode = new FP69ExpiryDate(LocalDate.parse("1990-01-23"));

        assertThat(fp69ReasonCode.toEdifact())
            .isEqualTo("DTM+962:19900123:102'");
    }

    @Test
    void fromEdifact() {
        assertThat(FP69ExpiryDate.fromString("DTM+962:19920225:102"))
            .isEqualTo(new FP69ExpiryDate(LocalDate.parse("1992-02-25")));
    }
}
