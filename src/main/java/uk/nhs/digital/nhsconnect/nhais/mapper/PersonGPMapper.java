package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonGP;

public class PersonGPMapper implements FromFhirToEdifactMapper<PersonGP> {

    public PersonGP map(Parameters parameters) {
        return PersonGP.builder()
                .practitioner(getPersonGP(parameters))
                .build();
    }

    private String getPersonGP(Parameters parameters) {
        Patient patient = getPatient(parameters);
        var reference = patient.getGeneralPractitionerFirstRep().getReference();

        return reference.split("/")[1];
    }
}
