package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PreviousHealthAuthorityName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PreviousHealthAuthorityNameMapper implements OptionalFromFhirToEdifactMapper<PreviousHealthAuthorityName> {

    public PreviousHealthAuthorityName map(Parameters parameters) {
        return new PreviousHealthAuthorityName(
            ParametersExtension.extractValue(parameters, ParameterNames.PREVIOUS_HA_CIPHER)
        );
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        return ParametersExtension.extractOptionalValue(parameters, ParameterNames.PREVIOUS_HA_CIPHER)
            .isPresent();
    }
}
