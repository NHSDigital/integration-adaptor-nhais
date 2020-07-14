package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ResidentialInstituteNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ResidentialInstituteExtension;

@Component
public class ResidentialInstituteNameAndAddressMapper implements OptionalFromFhirToEdifactMapper<ResidentialInstituteNameAndAddress> {

    public ResidentialInstituteNameAndAddress map(Parameters parameters) {
        return ResidentialInstituteNameAndAddress.builder()
            .identifier(getResidentialInstituteCode(parameters))
            .build();
    }

    private String getResidentialInstituteCode(Parameters parameters) {
        return ParametersExtension.extractExtension(parameters, ResidentialInstituteExtension.class)
            .map(ResidentialInstituteExtension::getValueString)
            .orElseThrow(() -> new FhirValidationException("Value of residential institute code is missing"));
    }

    @Override
    public boolean canMap(Parameters parameters) {
        return ParametersExtension.extractExtension(parameters, ResidentialInstituteExtension.class)
            .map(ResidentialInstituteExtension::getValueString)
            .filter(StringUtils::isNotBlank)
            .isPresent();
    }
}
