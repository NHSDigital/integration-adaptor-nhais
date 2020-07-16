package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FP69ReasonCodeTest {

    @Test
    void whenSettingNullCode_expectException() {
        assertThatThrownBy(() -> FP69ReasonCode.builder().code(null).build())
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void toEdifact() {
        var fp69ReasonCode = FP69ReasonCode.builder().code(123).build();

        assertThat(fp69ReasonCode.toEdifact())
            .isEqualTo("HEA+FRN+123:ZZZ'");
    }

    @Test
    void fromEdifact() {
        assertThat(FP69ReasonCode.fromString("HEA+FRN+8:ZZZ"))
            .isEqualTo(FP69ReasonCode.builder().code(8).build());
    }
}
