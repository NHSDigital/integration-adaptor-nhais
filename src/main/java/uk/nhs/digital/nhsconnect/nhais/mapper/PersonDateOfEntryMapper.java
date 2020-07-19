package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfEntry;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.LocalDate;

import static uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames.ENTRY_DATE;

@Component
public class PersonDateOfEntryMapper implements FromFhirToEdifactMapper<PersonDateOfEntry> {
    public PersonDateOfEntry map(Parameters parameters) {
        return new PersonDateOfEntry(getPersonEntryDate(parameters));
    }

    private LocalDate getPersonEntryDate(Parameters parameters) {
        return LocalDate.parse(
            ParametersExtension.extractValue(parameters, ENTRY_DATE)
        );
    }
}
