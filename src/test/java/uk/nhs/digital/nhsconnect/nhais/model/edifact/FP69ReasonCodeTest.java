package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FP69ReasonCodeTest {

    @Test
    void whenSettingNullCode_expectException() {
        assertThatThrownBy(() -> new FP69ReasonCode(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void toEdifact() {
        var fp69ReasonCode = new FP69ReasonCode(123);

        assertThat(fp69ReasonCode.toEdifact())
            .isEqualTo("HEA+FRN+123:ZZZ'");
    }

    @Test
    void fromEdifact() {
        assertThat(FP69ReasonCode.fromString("HEA+FRN+8:ZZZ"))
            .isEqualTo(new FP69ReasonCode(8));
    }
}
