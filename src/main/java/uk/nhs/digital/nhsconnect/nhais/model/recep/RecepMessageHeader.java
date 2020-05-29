package uk.nhs.digital.nhsconnect.nhais.model.recep;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

@Getter
@Setter
@RequiredArgsConstructor
public class RecepMessageHeader extends Segment {
    private static final String HEADER = "+RECEP:0:2:FH";

    private @NonNull Long sequenceNumber;

    @Override
    public String getKey() {
        return "UNH";
    }

    @Override
    public String getValue() {
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return formattedSequenceNumber
                .concat(HEADER);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        //TODO change Segment class to interface with default methods
    }

    @Override
    public void preValidate() {
    }
}

