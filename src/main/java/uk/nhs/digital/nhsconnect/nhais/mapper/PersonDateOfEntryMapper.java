package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfEntry;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames.ENTRY_DATE;

@Component
public class PersonDateOfEntryMapper implements FromFhirToEdifactMapper<PersonDateOfEntry> {
    public PersonDateOfEntry map(Parameters parameters) {
        return PersonDateOfEntry.builder()
            .timestamp(getPersonEntryDate(parameters))
            .build();
    }

    private Instant getPersonEntryDate(Parameters parameters) {
        return parseInstant(
            ParametersExtension.extractValue(parameters, ENTRY_DATE)
        );
    }

    private Instant parseInstant(String value) {
        LocalDate localDate = LocalDate.parse(value);
        return localDate.atStartOfDay(TimestampService.UKZone).toInstant();
    }
}
