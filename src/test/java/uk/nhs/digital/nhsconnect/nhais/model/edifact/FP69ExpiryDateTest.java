package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FP69ExpiryDateTest {

    @Test
    void whenSettingNullTimestamp_expectException() {
        assertThatThrownBy(() -> FP69ExpiryDate.builder().timestamp(null).build())
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void toEdifact() {
        var fp69ReasonCode = FP69ExpiryDate.builder().timestamp(Instant.ofEpochSecond(633052800)).build();

        assertThat(fp69ReasonCode.toEdifact())
            .isEqualTo("DTM+962:19900123:102'");
    }

    @Test
    void fromEdifact() {
        assertThat(FP69ExpiryDate.fromString("DTM+962:19920225:102"))
            .isEqualTo(FP69ExpiryDate.builder().timestamp(Instant.ofEpochSecond(698976000)).build());
    }
}
