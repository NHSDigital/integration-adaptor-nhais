package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class DrugsMarkerMapper implements OptionalFromFhirToEdifactMapper<DrugsMarker> {

    private final String AFFIRMATIVE_VALUE = "true";
    private final String NEGATIVE_VALUE = "false";

    public DrugsMarker map(Parameters parameters) {
        String markerValue = ParametersExtension.extractExtensionValue(parameters, DrugsMarkerExtension.URL)
            .orElse(NEGATIVE_VALUE);
        return new DrugsMarker(isTrue(markerValue));
    }

    private boolean isTrue(String markerValue) {
        return AFFIRMATIVE_VALUE.equals(markerValue);
    }

    @Override
    public boolean inputDataExists(Parameters parameters) {
        String markerValue = ParametersExtension.extractExtensionValue(parameters, DrugsMarkerExtension.URL)
            .orElse(NEGATIVE_VALUE);
        return isTrue(markerValue);
    }

}
