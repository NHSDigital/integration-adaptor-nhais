package uk.nhs.digital.nhsconnect.nhais.mapper;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import org.hl7.fhir.r4.model.Parameters;

public class AcceptanceTypeMapper implements FromFhirToEdifactMapper<AcceptanceType> {

    public AcceptanceType map(Parameters parameters) {
        return AcceptanceType.builder()
            .type(getAcceptanceType(parameters))
            .build();
    }

    private String getAcceptanceType(Parameters parameters) {
        return AcceptanceType.AvailableTypes.toCode(
            ParametersExtension.extractValue(parameters, ParameterNames.ACCEPTANCE_TYPE)
        );
    }
}
