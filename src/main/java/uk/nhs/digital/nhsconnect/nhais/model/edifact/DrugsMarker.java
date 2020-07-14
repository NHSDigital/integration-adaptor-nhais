package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

/**
 * Example HEA+DM+Y:ZZZ'
 */
@RequiredArgsConstructor
public class DrugsMarker extends Segment {
    private final static String KEY = "HEA";
    private final static String APT_PREFIX = "DM";
    private final static String ZZZ_SUFFIX = ":ZZZ";

    private final boolean drugsMarker;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return APT_PREFIX
            .concat(PLUS_SEPARATOR)
            .concat(parseDrugMarker())
            .concat(ZZZ_SUFFIX);
    }

    private String parseDrugMarker() {
        if(drugsMarker) {
            return "Y";
        }
        return "%";
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
    }

}
