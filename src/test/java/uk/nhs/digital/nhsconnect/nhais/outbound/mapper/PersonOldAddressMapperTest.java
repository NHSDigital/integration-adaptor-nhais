package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.inbound.fhir.PatientParameter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonOldAddress;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonOldAddressMapperTest {

    @Test
    void When_MappingAddress_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Address current = new Address();
        current.addLine("")
            .addLine("2 CROSSDALE COURT")
            .addLine("SEA CLIFF CRESCENT")
            .addLine("")
            .addLine("SCARBOROUGH");
        current.setPostalCode("YO11 2XZ");
        Address previous = new Address();
        previous.addLine("MOORSIDE FARM")
            .addLine("OLD LANE")
            .addLine("ST PAULS CRAY")
            .addLine("ORPINGTON")
            .addLine("KENT");
        patient.setAddress(List.of(current, previous));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personAddressOldMapper = new PersonOldAddressMapper();
        personAddressOldMapper.map(parameters);
        PersonOldAddress personOldAddress = personAddressOldMapper.map(parameters);

        var expectedPersonOldAddress = PersonOldAddress
            .builder()
            .addressLine1("MOORSIDE FARM")
            .addressLine2("OLD LANE")
            .addressLine3("ST PAULS CRAY")
            .addressLine4("ORPINGTON")
            .addressLine5("KENT")
            .build();

        assertEquals(expectedPersonOldAddress, personOldAddress);
    }

    @Test
    public void When_MappingWithoutAddress_Then_IllegalStateExceptionIsThrown() {
        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter());

        var addressOldMapper = new PersonOldAddressMapper();
        assertThrows(FhirValidationException.class, () -> addressOldMapper.map(parameters));
    }

    @Test
    public void When_MappingWithOnlyCurrentAddress_Then_IllegalStateExceptionIsThrown() {
        Patient patient = new Patient();
        Address current = new Address();
        current.addLine("")
            .addLine("2 CROSSDALE COURT")
            .addLine("SEA CLIFF CRESCENT")
            .addLine("")
            .addLine("SCARBOROUGH");
        current.setPostalCode("YO11 2XZ");
        patient.setAddress(List.of(current));

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var addressOldMapper = new PersonOldAddressMapper();
        assertThrows(FhirValidationException.class, () -> addressOldMapper.map(parameters));
    }
}
