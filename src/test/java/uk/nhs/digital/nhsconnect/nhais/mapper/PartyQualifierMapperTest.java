package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ManagingOrganizationIdentifier;
import uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir.PatientParameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void When_MappingWithoutPartyQualifier_Then_FhirValidationExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personHAMapper = new PartyQualifierMapper();
        assertThrows(FhirValidationException.class, () -> personHAMapper.map(parameters));
    }
}
