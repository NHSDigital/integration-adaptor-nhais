package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class AcceptanceTypeMapper implements FromFhirToEdifactMapper<AcceptanceType> {

    public AcceptanceType map(Parameters parameters) {
        return AcceptanceType.builder()
            .acceptanceType(ParametersExtension.extractAcceptanceType(parameters))
            .build();
    }
}
