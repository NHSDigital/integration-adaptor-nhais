package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ResidentialInstituteExtension;

@Getter
public class AmendmentStringExtension extends AmendmentExtension {

    @JsonProperty(value = "valueString")
    private final String value;

    @JsonCreator
    public AmendmentStringExtension(@JsonProperty(value = "url") String url,
                                    @JsonProperty(value = "valueString") String value) {
        super(url);
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }

    public static class Birthplace extends AmendmentStringExtension {

        public Birthplace(String value) {
            super(BirthPlaceExtension.URL, value);
        }
    }

    public static class ResidentialInstituteCode extends AmendmentStringExtension {

        public ResidentialInstituteCode(String value) {
            super(ResidentialInstituteExtension.URL, value);
        }
    }
}
