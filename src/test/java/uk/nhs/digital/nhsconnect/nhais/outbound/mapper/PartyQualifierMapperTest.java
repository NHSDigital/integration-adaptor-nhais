package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.PatientParameter;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class PartyQualifierMapperTest {

    @Test
    void When_MappingPartyQualifier_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        patient.setManagingOrganization(
            new Reference().setIdentifier(new ManagingOrganizationIdentifier("X11"))
        );

        Parameters parameters = new Parameters()
            .addParameter(new PatientParameter(patient));

        var personHAMapper = new PartyQualifierMapper();
        PartyQualifier partyQualifier = personHAMapper.map(parameters);

        var expectedPersonHA = PartyQualifier
            .builder()
            .organization("X11")
            .build();

        assertEquals(expectedPersonHA, partyQualifier);
    }

    @Test
    public void When_MappingWithoutPartyQualifier_Then_NullPointerExceptionIsThrown() {
        Parameters parameters = new Parameters();
        parameters.addParameter(new PatientParameter());

        var personHAMapper = new PartyQualifierMapper();
        assertThrows(NullPointerException.class, () -> personHAMapper.map(parameters));
    }
}
