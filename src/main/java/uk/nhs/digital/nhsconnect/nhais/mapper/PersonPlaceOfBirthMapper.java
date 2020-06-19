package uk.nhs.digital.nhsconnect.nhais.mapper;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPlaceOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PersonPlaceOfBirthMapper implements FromFhirToEdifactMapper<PersonPlaceOfBirth> {

    public PersonPlaceOfBirth map(Parameters parameters) {
        try {
            return PersonPlaceOfBirth.builder()
                .location(getPersonPlaceOfBirth(parameters))
                .build();
        } catch (RuntimeException ex) {
            throw new FhirValidationException(ex);
        }
    }

    private String getPersonPlaceOfBirth(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getExtensionByUrl(BirthPlaceExtension.URL).getValue().toString();
    }
}
