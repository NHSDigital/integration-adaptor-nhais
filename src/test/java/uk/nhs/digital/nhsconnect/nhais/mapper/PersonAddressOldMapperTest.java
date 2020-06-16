package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonOldAddress;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonAddressOldMapperTest {

    @Test
    void When_MappingAddress_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Address address = new Address();
        address.setUse(Address.AddressUse.OLD);
        address.addLine("Moorside Farm")
            .addLine("Old Lane")
            .addLine("St Pauls Cray")
            .addLine("Orpington")
            .addLine("Kent");
        patient.setAddress(List.of(address));

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personAddressOldMapper = new PersonAddressOldMapper();
        personAddressOldMapper.map(parameters);
        PersonOldAddress personOldAddress = personAddressOldMapper.map(parameters);

        var expectedPersonOldAddress = PersonOldAddress
            .builder()
            .addressLine1("Moorside Farm")
            .addressLine2("Old Lane")
            .addressLine3("St Pauls Cray")
            .addressLine4("Orpington")
            .addressLine5("Kent")
            .build();

        assertEquals(expectedPersonOldAddress, personOldAddress);
    }

    @Test
    public void When_MappingWithoutAddress_Then_NoSuchElementExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var addressOldMapper = new PersonAddressOldMapper();
        assertThrows(IllegalStateException.class, () -> addressOldMapper.map(parameters));
    }
}
