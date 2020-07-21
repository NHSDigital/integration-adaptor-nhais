package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;

@Getter
public class AmendmentBooleanExtension extends AmendmentExtension {

    @JsonProperty(value = "valueBoolean")
    private final boolean value;

    @JsonCreator
    public AmendmentBooleanExtension(@JsonProperty(value = "url") String url,
                                     @JsonProperty(value = "valueBoolean") boolean value) {
        super(url);
        this.value = value;
    }

    @Override
    public String get() {
        return String.valueOf(value);
    }

    public static class DrugsDispensedMarker extends AmendmentBooleanExtension {

        public DrugsDispensedMarker(boolean value) {
            super(DrugsMarkerExtension.URL, value);
        }
    }

}
