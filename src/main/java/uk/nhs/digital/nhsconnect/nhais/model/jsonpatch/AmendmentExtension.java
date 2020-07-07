package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;

public class AmendmentExtension implements AmendmentValue{
    @Getter(AccessLevel.PACKAGE)
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

        public static final String URL = "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-DrugsDispensedMarker";

        public DrugsDispensedMarker(AmendmentExtension amendmentExtension) {
            super(URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }

    public static class Birthplace extends AmendmentExtension {

        public static final String URL = "http://hl7.org/fhir/StructureDefinition/patient-birthPlace";

        public Birthplace(AmendmentExtension amendmentExtension) {
            super(URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }

    public static class ResidentialInstituteCode extends AmendmentExtension {

        public static final String URL = "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-ResidentialInstituteCode";

        public ResidentialInstituteCode(AmendmentExtension amendmentExtension) {
            super(URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }

    public static class PreviousGp extends AmendmentExtension {

        public static final String URL = "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-PreviousGP";

        public PreviousGp(AmendmentExtension amendmentExtension) {
            super(URL, amendmentExtension.valueString, amendmentExtension.valueBoolean);
        }
    }
}
