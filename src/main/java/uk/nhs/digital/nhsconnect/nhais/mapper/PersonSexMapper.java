package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PersonSexMapper implements FromFhirToEdifactMapper<PersonSex> {

    public PersonSex map(Parameters parameters) {
        try {
            return PersonSex.builder()
                .sexCode(getPersonSex(parameters))
                .build();
        } catch (RuntimeException ex) {
            throw new FhirValidationException(ex);
        }
    }

    private String getPersonSex(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return PersonSex.getGenderCode(patient);
    }
}
