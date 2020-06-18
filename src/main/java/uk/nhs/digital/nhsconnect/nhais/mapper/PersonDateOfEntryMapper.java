package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfEntry;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

public class PersonDateOfEntryMapper implements FromFhirToEdifactMapper<PersonDateOfEntry> {
    private final static String ENTRY_DATE_PARAM = "entryDate";

    public PersonDateOfEntry map(Parameters parameters) {
        return PersonDateOfEntry.builder()
            .timestamp(getPersonEntryDate(parameters))
            .build();
    }

    private Instant getPersonEntryDate(Parameters parameters) {
        return parseInstant(
            ParametersExtension.extractValue(parameters, ENTRY_DATE_PARAM)
        );
    }

    private Instant parseInstant(String value) {
        return ZonedDateTime.parse(value).toInstant();
    }
}
