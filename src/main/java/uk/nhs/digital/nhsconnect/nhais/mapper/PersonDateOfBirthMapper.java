package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.LocalDate;

@Component
public class PersonDateOfBirthMapper implements OptionalFromFhirToEdifactMapper<PersonDateOfBirth> {

    public PersonDateOfBirth map(Parameters parameters) {
        return PersonDateOfBirth.builder()
            .timestamp(getPersonDob(parameters))
            .build();
    }

    private Instant getPersonDob(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        var birthDateElement = patient.getBirthDateElement();
        return LocalDate
            .of(birthDateElement.getYear(), birthDateElement.getMonth() + 1, birthDateElement.getDay())
            .atStartOfDay(TimestampService.UKZone)
            .toInstant();
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getBirthDate() != null;
    }
}
