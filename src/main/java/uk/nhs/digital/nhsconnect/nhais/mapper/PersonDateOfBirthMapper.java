package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class PersonDateOfBirthMapper implements FromFhirToEdifactMapper<PersonDateOfBirth> {

    public PersonDateOfBirth map(Parameters parameters) {
        return PersonDateOfBirth.builder()
            .timestamp(getPersonDob(parameters))
            .build();
    }

    private Instant getPersonDob(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        var birthDateElement = patient.getBirthDateElement();
        return LocalDateTime
            .of(
                birthDateElement.getYear(), birthDateElement.getMonth() + 1, birthDateElement.getDay(),
                birthDateElement.getHour(), birthDateElement.getMinute(), birthDateElement.getSecond())
            .atZone(TimestampService.UKZone)
            .toInstant();
    }
}
