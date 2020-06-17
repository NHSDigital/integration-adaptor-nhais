package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.GeneralPractitionerIdentifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GpNameAndAddressMapperTest {

    @Test
    void When_MappingGP_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        patient.setGeneralPractitioner(List.of(
            new Reference().setIdentifier(new GeneralPractitionerIdentifier("4826940,281"))
        ));

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personGPMapper = new GpNameAndAddressMapper();
        GpNameAndAddress personGP = personGPMapper.map(parameters);

        var expectedPersonGP = GpNameAndAddress
            .builder()
            .identifier("4826940,281")
            .code("900")
            .build();

        assertEquals(expectedPersonGP.toEdifact(), personGP.toEdifact());
    }

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
