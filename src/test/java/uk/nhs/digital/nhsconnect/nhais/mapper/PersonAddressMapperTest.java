package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonAddressMapperTest {

    @Test
    void When_MappingAddress_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Address address = new Address();
        address.setUse(Address.AddressUse.HOME);
        address.addLine("534 Erewhon St")
            .addLine("PeasantVille")
            .addLine("Rainbow")
            .addLine("Vic 3999");
        patient.setAddress(List.of(address));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personAddressMapper = new PersonAddressMapper();
        PersonAddress personAddress = personAddressMapper.map(parameters);

        var expectedPersonAddress = PersonAddress
            .builder()
            .addressLine1("534 Erewhon St")
            .addressLine2("PeasantVille")
            .addressLine3("Rainbow")
            .addressLine4("Vic 3999")
            .build();

        assertEquals(expectedPersonAddress, personAddress);
    }

    @Test
    public void When_MappingWithoutAddress_Then_IllegalStateExceptionIsThrown() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        var personAddressMapper = new PersonAddressMapper();
        assertThrows(IllegalStateException.class, () -> personAddressMapper.map(parameters));
    }
}
