package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NhsIdentifierTest {

    @Test
    void testSystemIsNhsNumberSystem() {
        NhsIdentifier nhsIdentifier = new NhsIdentifier("some_nhs_number");
        assertThat(nhsIdentifier.getSystem()).isEqualTo("https://fhir.nhs.uk/Id/nhs-number");
    }

    @Test
    void testNhsNumberIsSetAsValue() {
        String nhsNumber = "some_nhs_number";
        NhsIdentifier nhsIdentifier = new NhsIdentifier(nhsNumber);
        assertThat(nhsIdentifier.getValue()).isEqualTo(nhsNumber);
    }
}