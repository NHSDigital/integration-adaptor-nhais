package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonAddressOldMapperTest {

    @Test
    void When_MappingAddress_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Address address = new Address();
        address.setUse(Address.AddressUse.OLD);
        address.setText("534 Erewhon St PeasantVille, Rainbow, Vic  3999");
        address.setLine(List.of(new StringType("534 Erewhon St")));
        patient.setAddress(List.of(address));

        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName(Patient.class.getSimpleName())
                .setResource(patient);

        var personAddressOldMapper = new PersonAddressOldMapper();
        personAddressOldMapper.map(parameters);
        PersonAddress personAddress = personAddressOldMapper.map(parameters);

        var expectedPersonAddress = PersonAddress
                .builder()
                .addressText("534 Erewhon St PeasantVille, Rainbow, Vic  3999")
                .addressLine1("534 Erewhon St")
                .build();

        assertEquals(expectedPersonAddress, personAddress);
    }

    @Test
    public void When_MappingWithoutAddress_Then_NoSuchElementExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
                .setName(Patient.class.getSimpleName())
                .setResource(patient);

        var addressOldMapper = new PersonAddressOldMapper();
        assertThrows(NoSuchElementException.class, () -> addressOldMapper.map(parameters));
    }
}
