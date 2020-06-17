package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;

public class PartyQualifierMapper implements FromFhirToEdifactMapper<PartyQualifier> {

    public PartyQualifier map(Parameters parameters) {
        return PartyQualifier.builder()
            .organization(getPersonHA(parameters))
            .build();
    }

    private String getPersonHA(Parameters parameters) {
        Patient patient = getPatient(parameters);
        return patient.getManagingOrganization().getIdentifier().getValue();
    }
}
