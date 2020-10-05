package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceDate;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.LocalDate;

@Component
public class AcceptanceDateMapper implements FromFhirToEdifactMapper<AcceptanceDate> {

    public AcceptanceDate map(Parameters parameters) {
        return new AcceptanceDate(getAcceptanceDate(parameters));
    }

    private LocalDate getAcceptanceDate(Parameters parameters) {
        String dateAsString = ParametersExtension.extractValue(parameters, ParameterNames.ACCEPTANCE_DATE);
        return LocalDate.parse(dateAsString);
    }
}
