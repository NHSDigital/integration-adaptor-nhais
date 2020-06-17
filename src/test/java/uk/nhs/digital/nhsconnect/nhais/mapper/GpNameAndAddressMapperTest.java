package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GpNameAndAddressMapperTest {

//    @Test
//    void When_MappingGP_Then_ExpectCorrectResult() {
//        Patient patient = new Patient();
//        Reference reference = new Reference();
//        reference.setReference("Practitioner/4826940,281");
//        patient.setGeneralPractitioner(List.of(reference));
//
//        Parameters parameters = new Parameters();
//        parameters.addParameter()
//            .setName(Patient.class.getSimpleName())
//            .setResource(patient);
//
//        var personGPMapper = new GpNameAndAddressMapper();
//        GpNameAndAddress personGP = personGPMapper.map(parameters);
//
//        var expectedPersonGP = GpNameAndAddress
//            .builder()
//            .identifier("4826940,281")
//            .code("900")
//            .build();
//
//        assertEquals(expectedPersonGP, personGP);
//    }

    @Test
    public void When_MappingWithoutGP_Then_NullPointerExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personGPMapper = new GpNameAndAddressMapper();
        assertThrows(NullPointerException.class, () -> personGPMapper.map(parameters));
    }
}
