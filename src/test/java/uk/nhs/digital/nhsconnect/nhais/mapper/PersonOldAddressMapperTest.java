package uk.nhs.digital.nhsconnect.nhais.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonOldAddress;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class PersonOldAddressMapperTest {

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

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personAddressOldMapper = new PersonOldAddressMapper();
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
    public void When_MappingWithoutAddress_Then_IllegalStateExceptionIsThrown() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        var addressOldMapper = new PersonOldAddressMapper();
        assertThrows(IllegalStateException.class, () -> addressOldMapper.map(parameters));
    }
}
