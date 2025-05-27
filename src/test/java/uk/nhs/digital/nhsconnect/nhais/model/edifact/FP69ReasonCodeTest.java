package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FP69ReasonCodeTest {
    private static final int REASON_CODE = 123;

    @Test
    void When_SettingNullCode_Expect_Exception() {
        assertThatThrownBy(() -> new FP69ReasonCode(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void toEdifact() {
        var fp69ReasonCode = new FP69ReasonCode(REASON_CODE);

        assertThat(fp69ReasonCode.toEdifact())
            .isEqualTo("HEA+FRN+" + REASON_CODE + ":ZZZ'");
    }

    @Test
    void fromEdifact() {
        assertThat(FP69ReasonCode.fromString("HEA+FRN+" + REASON_CODE+ ":ZZZ"))
            .isEqualTo(new FP69ReasonCode(REASON_CODE));
    }
}
