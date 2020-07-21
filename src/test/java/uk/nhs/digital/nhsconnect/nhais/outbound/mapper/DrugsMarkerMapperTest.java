package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.PatientParameter;

import static org.assertj.core.api.Assertions.assertThat;

class DrugsMarkerMapperTest {

    private final DrugsMarkerMapper drugsMarkerMapper = new DrugsMarkerMapper();

    @Test
    void when_DrugsMarkerExtensionExistsAndValueIsTrue_Then_CanMap() {
        Patient patient = new Patient();
        patient.addExtension(new DrugsMarkerExtension("true"));
        PatientParameter patientParameter = new PatientParameter(patient);
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        assertThat(drugsMarkerMapper.inputDataExists(parameters)).isTrue();
    }

    @Test
    void when_DrugsMarkerExtensionExistsAndValueIsFalse_Then_CanNotMap() {
        Patient patient = new Patient();
        patient.addExtension(new DrugsMarkerExtension("false"));
        PatientParameter patientParameter = new PatientParameter(patient);
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        assertThat(drugsMarkerMapper.inputDataExists(parameters)).isFalse();
    }

    @Test
    void when_DrugsMarkerExtensionDoesntExist_Then_CanNotMap() {
        PatientParameter patientParameter = new PatientParameter();
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        assertThat(drugsMarkerMapper.inputDataExists(parameters)).isFalse();
    }

    @Test
    void when_DrugsMarkerExtensionExistsAndValueIsTrue_Then_MappingSuccessful() {
        Patient patient = new Patient();
        patient.addExtension(new DrugsMarkerExtension("true"));
        PatientParameter patientParameter = new PatientParameter(patient);
        Parameters parameters = new Parameters()
            .addParameter(patientParameter);

        DrugsMarker drugsMarker = drugsMarkerMapper.map(parameters);

        DrugsMarker expected = new DrugsMarker(true);

        assertThat(drugsMarker.toEdifact()).isEqualTo(expected.toEdifact());
        assertThat(drugsMarker.toEdifact()).isEqualTo("HEA+DM+Y:ZZZ'");
    }
}