package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;

@Builder
@Data
public class PersonAddress extends Segment {
    /*
        Address - House Name Either Address line 1 or Address line 2 must be populated
        Address - Number/Road Name Either Address line 1 or Address line 2 must be populated
        Address - Locality
        Address - Post Town
        Address - County
        Address - Postcode Must be a valid format, must not be a ZZZ code
     */

    //Address : Moorside Farm, Old Lane, St Pauls Cray, Orpington, Kent, BR6 7EW.
    //NAD+PAT++MOORSIDE FARM:OLD LANE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7EW'

    private final static String PAT_PREFIX = "PAT";
    private @NonNull String addressText;
    //address Lines only for validation
    private String addressLine1;
    private String addressLine2;

    @Override
    // This segment may be used to provide the usual address of the patient.
    // A second repeat of the segment may be used to provide the previous address of the patient
    public String getKey() {
        return "NAD";
    }

    @Override
    public String getValue() {
        addressText = addressText.replace(", ", ":");

        return PAT_PREFIX
            .concat(PLUS_SEPARATOR)
            .concat(PLUS_SEPARATOR)
            .concat(StringUtils.upperCase(addressText));
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(addressText) || addressText.isBlank()) {
            throw new EdifactValidationException(getKey() + ": addressText is required");
        }

        if (Objects.isNull(addressLine1) && Objects.isNull(addressLine2)) {
            throw new EdifactValidationException("Address line 1 or Address line 2 must be populated");
        }

        if (addressLine1.isBlank() && addressLine2.isBlank()) {
            throw new EdifactValidationException("Address line 1 or Address line 2 must be populated");
        }
    }
}
