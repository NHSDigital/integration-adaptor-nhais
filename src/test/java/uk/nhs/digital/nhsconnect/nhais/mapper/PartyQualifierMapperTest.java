package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartyQualifierMapperTest {

    @Test
    void When_MappingPartyQualifier_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Identifier identifier = new Identifier();
        identifier.setSystem("https://digital.nhs.uk/services/nhais/guide-to-nhais-gp-links-documentation");
        identifier.setValue("XX1");
        Reference reference = new Reference();
        reference.setIdentifier(identifier);
        patient.setManagingOrganization(reference);

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personHAMapper = new PartyQualifierMapper();
        PartyQualifier partyQualifier = personHAMapper.map(parameters);

        var expectedPersonHA = PartyQualifier
            .builder()
            .organization("XX1")
            .build();

        assertEquals(expectedPersonHA, partyQualifier);
    }

    @Test
    public void When_MappingWithoutPartyQualifier_Then_NullPointerExceptionIsThrown() {
        Patient patient = new Patient();

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(Patient.class.getSimpleName())
            .setResource(patient);

        var personHAMapper = new PartyQualifierMapper();
        assertThrows(NullPointerException.class, () -> personHAMapper.map(parameters));
    }
}
