package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.LocalDate;

@Component
public class PersonDateOfBirthMapper implements OptionalFromFhirToEdifactMapper<PersonDateOfBirth> {

    public PersonDateOfBirth map(Parameters parameters) {
        return PersonDateOfBirth.builder()
            .dateOfBirth(getPersonDob(parameters))
            .build();
    }

    private LocalDate getPersonDob(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        var birthDateElement = patient.getBirthDateElement();
        return LocalDate.of(birthDateElement.getYear(), birthDateElement.getMonth() + 1, birthDateElement.getDay());
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getBirthDate() != null;
    }
}
