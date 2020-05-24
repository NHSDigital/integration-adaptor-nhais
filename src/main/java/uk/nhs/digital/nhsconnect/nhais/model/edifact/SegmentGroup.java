package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *class declaration:
 */
@Getter @Setter @RequiredArgsConstructor
public class SegmentGroup extends Segment{

    private @NonNull Integer segmentGroupNumber;

    @Override
    public String getKey() {
        return "S"+String.format("%02d", segmentGroupNumber);
    }

    @Override
    public String getValue() {
        return segmentGroupNumber.toString();
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if(!(segmentGroupNumber == 1 || segmentGroupNumber == 2)){
            throw new EdifactValidationException("S: Attribute segment_group_number must be 1 or 2");
        }
    }
}
