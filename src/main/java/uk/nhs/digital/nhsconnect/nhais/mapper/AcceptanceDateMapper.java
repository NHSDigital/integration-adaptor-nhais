package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class AcceptanceDateMapper implements FromFhirToEdifactMapper<DateTimePeriod> {

    public DateTimePeriod map(Parameters parameters) {
        return new DateTimePeriod(getAcceptanceDate(parameters), DateTimePeriod.TypeAndFormat.ACCEPTANCE_DATE);
    }

    private Instant getAcceptanceDate(Parameters parameters) {
        String dateAsString = ParametersExtension.extractValue(parameters, ParameterNames.ACCEPTANCE_DATE);
        return LocalDate.parse(dateAsString).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
