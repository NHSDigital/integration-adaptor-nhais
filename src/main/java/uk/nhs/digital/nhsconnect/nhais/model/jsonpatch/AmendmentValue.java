package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;

public interface AmendmentValue {

    String get();

    @JsonCreator
    static AmendmentValue from(String input){
        return new AmendmentSimpleValue(input);
    }
    @JsonCreator
    static AmendmentValue from(AmendmentExtension input){
        switch (input.getUrl()) {
            case AmendmentExtension.DrugsDispensedMarker.URL:
                return new AmendmentExtension.DrugsDispensedMarker(input);
            case AmendmentExtension.ResidentialInstituteCode.URL:
                return new AmendmentExtension.ResidentialInstituteCode(input);
            case AmendmentExtension.Birthplace.URL:
                return new AmendmentExtension.Birthplace(input);
            case AmendmentExtension.PreviousGp.URL:
                return new AmendmentExtension.PreviousGp(input);
        }
        return new AmendmentSimpleValue(input.get());
    }
}
