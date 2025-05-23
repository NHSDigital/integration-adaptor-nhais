package uk.nhs.digital.nhsconnect.nhais.outbound;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FhirElementsUtilsTest {

    @Test
    public void When_NullGpCodeObject_Expect_ThrowsFhirValidationException() {
        Patient patient = new Patient();
        patient.setGeneralPractitioner(null);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void When_EmptyGpCodeList_Expect_ThrowsFhirValidationException() {
        Patient patient = new Patient();
        List<Reference> generalPractitioners = Collections.emptyList();
        patient.setGeneralPractitioner(generalPractitioners);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void When_BlankGpCodeString_Expect_ThrowsFhirValidationException() {
        Patient patient = new Patient();
        Reference reference = new Reference();
        Identifier identifier = new Identifier();
        identifier.setValue("");
        reference.setIdentifier(identifier);
        List<Reference> generalPractitioners = List.of(new Reference());
        patient.setGeneralPractitioner(generalPractitioners);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void When_NullHaCipherObject_Expect_ThrowsFhirValidationException() {
        Patient patient = new Patient();
        patient.setManagingOrganization(null);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void When_EmptyHaCipherNullIdentifier_Expect_ThrowsFhirValidationException() {
        Patient patient = new Patient();
        Reference managingOrganization = new Reference();
        patient.setManagingOrganization(managingOrganization);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void When_BlankHaCipherString_Expect_ThrowsFhirValidationException() {
        Patient patient = new Patient();
        Reference managingOrganization = new Reference();
        Identifier identifier = new Identifier();
        identifier.setValue("");
        managingOrganization.setIdentifier(identifier);
        patient.setManagingOrganization(managingOrganization);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }
}