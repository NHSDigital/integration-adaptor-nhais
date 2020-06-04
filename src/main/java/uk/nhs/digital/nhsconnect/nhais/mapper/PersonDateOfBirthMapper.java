package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;

import java.time.Instant;

public class PersonDateOfBirthMapper implements FromFhirToEdifactMapper<PersonDateOfBirth> {

    public PersonDateOfBirth map(Parameters parameters) {
        return PersonDateOfBirth.builder()
                .timestamp(getPersonDob(parameters))
                .build();
    }

    private Instant getPersonDob(Parameters parameters) {
        Patient patient = getPatient(parameters);
        return patient.getBirthDate().toInstant();
    }
}
