package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;

@Getter
public class AmendmentBooleanExtension extends AmendmentExtension {

    @JsonProperty(value = "valueBoolean")
    private final String value;

    @JsonCreator
    public AmendmentBooleanExtension(@JsonProperty(value = "url") String url,
                                     @JsonProperty(value = "valueBoolean") String value) {
        super(url);
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }

    public static class DrugsDispensedMarker extends AmendmentBooleanExtension {

        public DrugsDispensedMarker(String value) {
            super(DrugsMarkerExtension.URL, value);
        }
    }

}
