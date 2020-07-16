package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionDate;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class DeductionDateMapper implements FromFhirToEdifactMapper<DeductionDate> {

    public DeductionDate map(Parameters parameters) {
        return new DeductionDate(getDeductionDate(parameters));
    }

    private LocalDate getDeductionDate(Parameters parameters) {
        String dateAsString = ParametersExtension.extractValue(parameters, ParameterNames.DATE_OF_DEDUCTION);
        return LocalDate.parse(dateAsString);
    }
}
