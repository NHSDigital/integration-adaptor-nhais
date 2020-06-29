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
}
