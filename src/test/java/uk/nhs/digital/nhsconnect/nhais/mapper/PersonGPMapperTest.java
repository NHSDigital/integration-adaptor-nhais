package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonGP;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonGPMapperTest {

    @Test
    void When_MappingGP_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Reference reference = new Reference();
        reference.setReference("Practitioner/2750922,295");
        patient.setGeneralPractitioner(List.of(reference));

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personGPMapper = new PersonGPMapper();
        PersonGP personGP = personGPMapper.map(parameters);

        var expectedPersonGP = PersonGP
            .builder()
            .practitioner("2750922,295")
            .build();

        assertEquals(expectedPersonGP, personGP);

    }

    @Test
    public void When_MappingWithoutGP_Then_NullPointerExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personGPMapper = new PersonGPMapper();
        assertThrows(NullPointerException.class, () -> personGPMapper.map(parameters));
    }
}
