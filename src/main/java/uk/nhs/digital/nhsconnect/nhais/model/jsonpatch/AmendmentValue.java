package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.BirthPlaceExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.DrugsMarkerExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PreviousGpExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ResidentialInstituteExtension;

public interface AmendmentValue {

    String get();

    @JsonCreator
    static AmendmentValue from(String input){
        return new AmendmentSimpleValue(input);
    }
    @JsonCreator
    static AmendmentValue from(AmendmentExtension input){
        switch (input.getUrl()) {
            case DrugsMarkerExtension.URL:
                return new AmendmentExtension.DrugsDispensedMarker(input);
            case ResidentialInstituteExtension.URL:
                return new AmendmentExtension.ResidentialInstituteCode(input);
            case BirthPlaceExtension.URL:
                return new AmendmentExtension.Birthplace(input);
            case PreviousGpExtension.URL:
                return new AmendmentExtension.PreviousGp(input);
        }
        return new AmendmentSimpleValue(input.get());
    }
}
