package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

/**
 * Example HEA+DM+Y:ZZZ'
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DrugsMarker extends Segment {
    private final static String KEY = "HEA";
    private final static String APT_PREFIX = "DM";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    public final static String KEY_PREFIX = KEY + PLUS_SEPARATOR + APT_PREFIX;

    private final boolean drugsMarker;

    public boolean getDrugsMarker() {
        return  drugsMarker;
    }

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
        if (drugsMarker) {
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

    public static DrugsMarker fromString(String edifactString) {
        if (!edifactString.startsWith(DrugsMarker.KEY_PREFIX)) {
            throw new IllegalArgumentException("Can't create " + DrugsMarker.class.getSimpleName() + " from " + edifactString);
        }
        String[] plusSplit = Split.byPlus(edifactString);
        String marker = Split.byColon(plusSplit[2])[0];
        if (marker.equals("Y")) {
            return new DrugsMarker(true);
        } else if (marker.equals("%")) {
            return new DrugsMarker(false);
        } else {
            throw new EdifactValidationException("Value: '" + marker + "' of drug marker provided in edifact is not permitted");
        }
    }

}
