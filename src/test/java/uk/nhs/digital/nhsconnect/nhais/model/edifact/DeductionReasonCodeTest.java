package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DeductionReasonCodeTest {

    @Test
    public void When_MappingToEdifact_Then_ReturnCorrectString() {
        var expectedValue = "GIS+11:ZZZ'";

        var deductionReasonCode = DeductionReasonCode.builder()
            .code("11")
            .build();

        assertThat(deductionReasonCode.toEdifact()).isEqualTo(expectedValue);
    }

    @Test
    public void When_MappingToEdifactWithEmptyType_Then_EdifactValidationExceptionIsThrown() {
        var deductionReasonCode = DeductionReasonCode.builder()
            .code("")
            .build();

        assertThatThrownBy(deductionReasonCode::toEdifact).isInstanceOf(EdifactValidationException.class);
    }

    @Test
    void When_fromStringWithValidInput_Then_SegmentCreated() {
        DeductionReasonCode deductionReasonCode = DeductionReasonCode.fromString("GIS+1:ZZZ");
        DeductionReasonCode expectedDeductionReasonCode = new DeductionReasonCode("1");

        assertThat(deductionReasonCode.getValue()).isEqualTo(expectedDeductionReasonCode.getValue());
    }
    @Test
    void When_fromStringWithInvalidInput_Then_ExceptionThrown() {
        assertThatThrownBy(() -> DeductionReasonCode.fromString("XXX+1:ZZZ"))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
