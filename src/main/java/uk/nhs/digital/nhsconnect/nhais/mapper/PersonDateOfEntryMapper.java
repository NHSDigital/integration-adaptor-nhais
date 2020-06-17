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
        ParametersExtension parametersExt = new ParametersExtension(parameters);
        return parseInstant(
            parametersExt.extractValueOrThrow(ENTRY_DATE_PARAM,() -> new FhirValidationException("Error while parsing param: " + ENTRY_DATE_PARAM))
        );
    }

    private Instant parseInstant(String value) {
        //TODO use TimestampService
        return ZonedDateTime.parse(value).toInstant();
    }
}
