package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfEntry;

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
        return parameters.getParameter()
            .stream()
            .filter(param -> ENTRY_DATE_PARAM.equalsIgnoreCase(param.getName()))
            .map(Parameters.ParametersParameterComponent::getValue)
            .map(Objects::toString)
            .map(this::parseInstant)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Entry Date mapping problem"));
    }

    private Instant parseInstant(String value) {
        //TODO use TimestampService
        return ZonedDateTime.parse(value).toInstant();
    }
}
