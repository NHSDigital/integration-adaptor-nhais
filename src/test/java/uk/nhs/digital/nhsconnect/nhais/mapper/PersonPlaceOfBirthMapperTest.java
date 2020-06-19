package uk.nhs.digital.nhsconnect.nhais.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPlaceOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class PersonPlaceOfBirthMapperTest {

    @Test
    void testMappingBirthPlaceFromFhirToEdifactSegment() {
        Patient patient = new Patient();
        patient.addExtension(new BirthPlaceExtension("birth place"));
        Parameters parameters = new Parameters().addParameter(new PatientParameter(patient));

        PersonPlaceOfBirth placeOfBirth = new PersonPlaceOfBirthMapper().map(parameters);
        assertThat(placeOfBirth.toEdifact()).isEqualTo("LOC+950+birth place'");
    }
}