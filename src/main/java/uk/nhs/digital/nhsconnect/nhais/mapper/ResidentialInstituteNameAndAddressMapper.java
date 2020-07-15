package uk.nhs.digital.nhsconnect.nhais.mapper;

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
        return ParametersExtension.extractExtensionValue(parameters, ResidentialInstituteExtension.URL)
            .orElseThrow(() -> new FhirValidationException("Value of residential institute code is missing"));
    }

    @Override
    public boolean canMap(Parameters parameters) {
        return ParametersExtension.extractExtensionValue(parameters, ResidentialInstituteExtension.URL)
            .isPresent();
    }
}
