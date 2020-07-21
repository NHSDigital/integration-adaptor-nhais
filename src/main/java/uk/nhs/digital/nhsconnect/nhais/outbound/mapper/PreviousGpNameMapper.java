package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PreviousGpName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PreviousGpNameMapper implements OptionalFromFhirToEdifactMapper<PreviousGpName> {

    public PreviousGpName map(Parameters parameters) {
        return PreviousGpName.builder()
            .partyName(getPersonPreviousGP(parameters))
            .build();
    }

    private String getPersonPreviousGP(Parameters parameters) {
        return ParametersExtension.extractValue(parameters, ParameterNames.PREVIOUS_GP_NAME);
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        return ParametersExtension.extractOptionalValue(parameters, ParameterNames.PREVIOUS_GP_NAME)
            .isPresent();
    }
}
