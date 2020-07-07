package uk.nhs.digital.nhsconnect.nhais.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class FhirElementsUtilsTest {

    @Test
    public void whenNullGpCodeObject_theThrowsFhirValidationException() {
        Patient patient = new Patient();
        patient.setGeneralPractitioner(null);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void whenEmptyGpCodeList_thenThrowsFhirValidationException() {
        Patient patient = new Patient();
        List<Reference> generalPractitioners = Collections.emptyList();
        patient.setGeneralPractitioner(generalPractitioners);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void whenBlankGpCodeString_thenThrowsFhirValidationException() {
        Patient patient = new Patient();
        Reference reference = new Reference();
        Identifier identifier = new Identifier();
        identifier.setValue("");
        reference.setIdentifier(identifier);
        List<Reference> generalPractitioners = Collections.singletonList(new Reference());
        patient.setGeneralPractitioner(generalPractitioners);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void whenNullHaCipherObject_theThrowsFhirValidationException() {
        Patient patient = new Patient();
        patient.setManagingOrganization(null);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void whenEmptyHaCipherNullIdentifier_thenThrowsFhirValidationException() {
        Patient patient = new Patient();
        Reference managingOrganization = new Reference();
        patient.setManagingOrganization(managingOrganization);
        assertThatThrownBy(() -> FhirElementsUtils.checkGpCodePresence(patient))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void whenBlankHaCipherString_thenThrowsFhirValidationException() {
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