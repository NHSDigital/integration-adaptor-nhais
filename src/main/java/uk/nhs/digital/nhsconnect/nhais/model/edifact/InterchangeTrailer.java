package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 * A specialisation of a segment for the specific use case of an interchange trailer
 * takes in specific values required to generate an interchange trailer
 * example: UNZ+1+00000002'
 */
@Getter @Setter @RequiredArgsConstructor
public class InterchangeTrailer extends Segment {

    private @NonNull Integer numberOfMessages;
    private Long sequenceNumber;

    @Override
    public String getKey() {
        return "UNZ";
    }

    @Override
    public String getValue() {
        String formattedSequenceNumber = String.format("%08d", sequenceNumber);
        return numberOfMessages+"+"+formattedSequenceNumber;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (sequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
        if(sequenceNumber <= 0){
            throw new EdifactValidationException(getKey() + ": Attribute sequenceNumber is required");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        /*
        from Recep13.doc
        7	Examples of Receipt Reports

        The following example is of a receipt report sent from an FHSA (FHS1) to a GP practice (GP05),
        showing correct receipt of interchange 00000001,
        which contained messages 00000001, 00000002, 00000003, and 00000004.

        UNB+UNOA:2+FHS1+GP05+930520:1400+00000064++RECEP+++EDIFACT TRANSFER'
        UNH+00000028+RECEP:0:2:FH'
        BGM++600+243:199305201355:306+64'
        NHS+FHS:819:201+123456:814:202'
        DTM+815:199305190600:306'
        RFF+MIS:00000001 CP'
        RFF+MIS:00000002 CP'
        RFF+MIS:00000003 CP'
        RFF+MIS:00000004 CP'
        RFF+RIS:00000001 OK:4'
        UNT+10+00000028'
        UNZ+1+00000064'

        numberOfMessages is always 1 for RECEP
        numberOfMessages > 1 for EDIFACT ? RFF+RIS = OK:4
         */
        if(numberOfMessages != 1){
            throw new EdifactValidationException(getKey() + ": Attribute numberOfMessages is required");
        }
    }
}
