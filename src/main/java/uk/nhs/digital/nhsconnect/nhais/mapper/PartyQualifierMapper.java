package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

public class PartyQualifierMapper implements FromFhirToEdifactMapper<PartyQualifier> {

    public PartyQualifier map(Parameters parameters) {
        return PartyQualifier.builder()
            .organization(getPersonHA(parameters))
            .build();
    }

    private String getPersonHA(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getManagingOrganization().getIdentifier().getValue();
    }
}
