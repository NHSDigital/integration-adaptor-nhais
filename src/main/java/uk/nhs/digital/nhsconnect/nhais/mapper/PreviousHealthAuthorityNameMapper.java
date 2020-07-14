package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PreviousHealthAuthorityNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PreviousHealthAuthorityNameMapper implements OptionalFromFhirToEdifactMapper<PreviousHealthAuthorityNameAndAddress> {

    public PreviousHealthAuthorityNameAndAddress map(Parameters parameters) {
        return new PreviousHealthAuthorityNameAndAddress(
            ParametersExtension.extractValue(parameters, ParameterNames.PREVIOUS_HA_CIPHER)
        );
    }

    @Override
    public boolean canMap(Parameters parameters) {
        return ParametersExtension.extractOptionalValue(parameters, ParameterNames.PREVIOUS_HA_CIPHER)
            .filter(StringUtils::isNotBlank)
            .isPresent();
    }
}
