package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonHA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonHAMapperTest {

    @Test
    void When_MappingHA_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Reference reference = new Reference();
        reference.setReference("Organization/XX1");
        patient.setManagingOrganization(reference);

        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName(Patient.class.getSimpleName())
                .setResource(patient);

        var personHAMapper = new PersonHAMapper();
        PersonHA personHA = personHAMapper.map(parameters);

        var expectedPersonHA = PersonHA
                .builder()
                .organization("XX1")
                .build();

        assertEquals(expectedPersonHA, personHA);

    }

    @Test
    public void When_MappingWithoutHA_Then_NullPointerExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName(Patient.class.getSimpleName())
                .setResource(patient);

        var personHAMapper = new PersonHAMapper();
        assertThrows(NullPointerException.class, () -> personHAMapper.map(parameters));
    }
}
