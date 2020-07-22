package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PersonPreviousNameMapper implements OptionalFromFhirToEdifactMapper<PersonPreviousName> {

    public PersonPreviousName map(Parameters parameters) {

        Patient patient = ParametersExtension.extractPatient(parameters);

        HumanName previousName = patient.getName()
            .stream()
            .limit(2)
            .skip(1)
            .findFirst()
            .orElseThrow(() -> new FhirValidationException("Previous name is not defined in request params"));

        return PersonPreviousName.builder()
            .familyName(previousName.getFamily())
            .build();
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getName().size() > 1;
    }
}
