package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * This segment is used to provide a code for the message which indicates its use. It is a constant of EDIFACT
 * example: BGM+++507'
 */
@Getter @Setter @RequiredArgsConstructor
public class BeginningOfMessage extends Segment{
    @Override
    public String getKey() {
        return "BGM";
    }

    @Override
    public String getValue() {
        return "++507";
    }

    @Override
    protected void validateStateful() {
        // Do nothing
    }

    @Override
    public void preValidate() {
        // Do nothing
    }
}
