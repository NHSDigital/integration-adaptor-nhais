package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionDate;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.LocalDate;

@Component
public class DeductionDateMapper implements FromFhirToEdifactMapper<DeductionDate> {

    public DeductionDate map(Parameters parameters) {
        return new DeductionDate(getDeductionDate(parameters));
    }

    private LocalDate getDeductionDate(Parameters parameters) {
        return ParametersExtension.extractOptionalValue(parameters, ParameterNames.DATE_OF_DEDUCTION)
            .filter(StringUtils::isNotBlank)
            .map(LocalDate::parse)
            .orElseThrow(() -> new FhirValidationException("Parameter " + ParameterNames.DATE_OF_DEDUCTION + " is required"));
    }
}
