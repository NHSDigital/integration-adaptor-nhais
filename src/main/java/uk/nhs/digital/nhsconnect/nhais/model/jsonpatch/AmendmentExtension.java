package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PreviousGpExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ResidentialInstituteExtension;

@Getter
public class AmendmentExtension implements AmendmentValue {
    private final String url;
    private final String valueString;
    private final String valueBoolean;

    @JsonCreator
    public AmendmentExtension(@JsonProperty(value = "url") String url,
                              @JsonProperty(value = "valueString") String valueString,
                              @JsonProperty(value = "valueBoolean") String valueBoolean) {
        this.url = url;
        this.valueString = valueString;
        this.valueBoolean = valueBoolean;
    }

    @Override
    public String get() {
        return valueString != null ? valueString : valueBoolean;
    }

    public static class DrugsDispensedMarker extends AmendmentExtension {

        public DrugsDispensedMarker(AmendmentExtension amendmentExtension) {
            super(DrugsMarkerExtension.URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }

    public static class Birthplace extends AmendmentExtension {

        public Birthplace(AmendmentExtension amendmentExtension) {
            super(BirthPlaceExtension.URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }

    public static class ResidentialInstituteCode extends AmendmentExtension {

        public ResidentialInstituteCode(AmendmentExtension amendmentExtension) {
            super(ResidentialInstituteExtension.URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }

    public static class PreviousGp extends AmendmentExtension {

        public PreviousGp(AmendmentExtension amendmentExtension) {
            super(PreviousGpExtension.URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }
}
