package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.Optional;

@Component
public class FreeTextMapper implements OptionalFromFhirToEdifactMapper<FreeText> {

    private static final String FREE_TEXT_VALUE_NAME = "freeText";

    public FreeText map(Parameters parameters) {
        return new FreeText(ParametersExtension.extractValue(parameters, FREE_TEXT_VALUE_NAME));
    }

    @Override
    public boolean canMap(Parameters parameters) {
        Optional<String> freeText = ParametersExtension.extractOptionalValue(parameters, FREE_TEXT_VALUE_NAME);
        return freeText
            .filter(StringUtils::isNotBlank)
            .isPresent();
    }

}
