package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DrugsMarker;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class DrugsMarkerMapper implements OptionalFromFhirToEdifactMapper<DrugsMarker> {

    public DrugsMarker map(Parameters parameters) {
        String markerValue = ParametersExtension.extractExtension(parameters, DrugsMarkerExtension.class)
            .map(DrugsMarkerExtension::getValueString)
            .orElse("false");
        return new DrugsMarker(isTrue(markerValue));
    }

    private boolean isTrue(String markerValue) {
        return "true".equals(markerValue);
    }

    @Override
    public boolean canMap(Parameters parameters) {
        String markerValue = ParametersExtension.extractExtension(parameters, DrugsMarkerExtension.class)
            .map(DrugsMarkerExtension::getValueString)
            .orElse("false");
        return isTrue(markerValue);
    }

}
