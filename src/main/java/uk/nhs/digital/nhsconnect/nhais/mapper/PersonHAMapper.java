package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonHA;

public class PersonHAMapper implements FromFhirToEdifactMapper<PersonHA> {

    public PersonHA map(Parameters parameters) {
        return PersonHA.builder()
            .organization(getPersonHA(parameters))
            .build();
    }

    private String getPersonHA(Parameters parameters) {
        Patient patient = getPatient(parameters);
        var reference = patient.getManagingOrganization().getReference();

        return reference.split("/")[1];
    }
}
