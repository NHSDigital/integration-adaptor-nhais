package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPlaceOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PersonPlaceOfBirthMapper implements OptionalFromFhirToEdifactMapper<PersonPlaceOfBirth> {

    public PersonPlaceOfBirth map(Parameters parameters) {
        return PersonPlaceOfBirth.builder()
            .location(getPersonPlaceOfBirth(parameters))
            .build();
    }

    private String getPersonPlaceOfBirth(Parameters parameters) {
        return ParametersExtension.extractExtension(parameters, BirthPlaceExtension.class)
            .map(BirthPlaceExtension::getValueString)
            .orElseThrow(() -> new FhirValidationException("Birthplace extension is missing value"));
    }

    @Override
    public boolean canMap(Parameters parameters) {
        return ParametersExtension.extractExtension(parameters, BirthPlaceExtension.class)
            .map(BirthPlaceExtension::getValueString)
            .filter(StringUtils::isNotBlank)
            .isPresent();
    }
}
