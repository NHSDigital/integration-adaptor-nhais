package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

public class AcceptanceTypeMapper implements FromFhirToEdifactMapper<AcceptanceType> {
    private final static String ACCEPTANCE_TYPE = "acceptanceType";

    public AcceptanceType map(Parameters parameters) {
        return AcceptanceType.builder()
            .type(getAcceptanceType(parameters))
            .build();
    }

    private String getAcceptanceType(Parameters parameters) {
        ParametersExtension parametersExt = new ParametersExtension(parameters);
        return parametersExt.extractValueOrThrow(ACCEPTANCE_TYPE,
            () -> new FhirValidationException("Error while parsing param: " + ACCEPTANCE_TYPE)
        );
    }
}
