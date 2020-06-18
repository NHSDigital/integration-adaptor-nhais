package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.Instant;

@Component
public class PersonDateOfBirthMapper implements FromFhirToEdifactMapper<PersonDateOfBirth> {

    public PersonDateOfBirth map(Parameters parameters) {
        return PersonDateOfBirth.builder()
            .timestamp(getPersonDob(parameters))
            .build();
    }

    private Instant getPersonDob(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getBirthDate().toInstant();
    }
}
