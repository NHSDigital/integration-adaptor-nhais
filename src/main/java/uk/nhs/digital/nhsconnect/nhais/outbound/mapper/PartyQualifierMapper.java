package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PartyQualifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
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
