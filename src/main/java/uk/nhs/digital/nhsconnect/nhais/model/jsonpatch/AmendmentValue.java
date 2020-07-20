package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ResidentialInstituteExtension;

public interface AmendmentValue {

    String get();

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    static AmendmentValue from(String input){
        return new AmendmentSimpleValue(input);
    }
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    static AmendmentValue from(@JsonProperty(value = "url") String url,
                               @JsonProperty(value = "valueBoolean") boolean valueBoolean,
                               @JsonProperty(value = "valueString") String valueString){
        switch (url) {
            case DrugsMarkerExtension.URL:
                return new AmendmentBooleanExtension.DrugsDispensedMarker(valueBoolean);
            case ResidentialInstituteExtension.URL:
                return new AmendmentStringExtension.ResidentialInstituteCode(valueString);
            case BirthPlaceExtension.URL:
                return new AmendmentStringExtension.Birthplace(valueString);
            default:
                throw new PatchValidationException("Unknown extension type using url " + url);
        }
    }
}
