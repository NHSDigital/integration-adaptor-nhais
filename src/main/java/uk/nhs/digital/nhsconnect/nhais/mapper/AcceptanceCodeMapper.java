package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceCode;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

public class AcceptanceCodeMapper implements FromFhirToEdifactMapper<AcceptanceCode> {
    private final static String ACCEPTANCE_CODE = "acceptanceCode";

    public AcceptanceCode map(Parameters parameters) {
        return AcceptanceCode.builder()
            .code(getAcceptanceCode(parameters))
            .build();
    }

    private String getAcceptanceCode(Parameters parameters) {
        ParametersExtension parametersExt = new ParametersExtension(parameters);
        return parametersExt.extractValueOrThrow(ACCEPTANCE_CODE,
            () -> new FhirValidationException("Error while parsing param: " + ACCEPTANCE_CODE)
        );
    }
}
