package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfExit;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.LocalDate;

@Component
public class PersonDateOfExitMapper implements OptionalFromFhirToEdifactMapper<PersonDateOfExit> {
    public PersonDateOfExit map(Parameters parameters) {
        return new PersonDateOfExit(getPersonEntryDate(parameters));
    }

    private LocalDate getPersonEntryDate(Parameters parameters) {
        return LocalDate.parse(ParametersExtension.extractValue(parameters, ParameterNames.EXIT_DATE));
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        return ParametersExtension.extractOptionalValue(parameters, ParameterNames.EXIT_DATE)
            .isPresent();
    }
}
