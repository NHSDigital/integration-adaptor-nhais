package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfExit;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.LocalDate;

@Component
public class PersonDateOfExitMapper implements OptionalFromFhirToEdifactMapper<PersonDateOfExit> {
    public PersonDateOfExit map(Parameters parameters) {
        return PersonDateOfExit.builder()
            .timestamp(getPersonEntryDate(parameters))
            .build();
    }

    private Instant getPersonEntryDate(Parameters parameters) {
        return parseInstant(
            ParametersExtension.extractValue(parameters, ParameterNames.EXIT_DATE)
        );
    }

    private Instant parseInstant(String value) {
        LocalDate localDate = LocalDate.parse(value);
        return localDate.atStartOfDay(TimestampService.UKZone).toInstant();
    }

    @Override
    public boolean canMap(Parameters parameters) {
        return ParametersExtension.extractOptionalValue(parameters, ParameterNames.EXIT_DATE)
            .map(this::parseInstant)
            .isPresent();
    }
}
