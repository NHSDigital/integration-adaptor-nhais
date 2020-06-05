package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

public abstract class Segment {

    private static final String TERMINATOR = "'";

    /**
     * @return the key of the segment for example NAD, DTM
     */
    public abstract String getKey();

    /**
     * @return the value portion of the segment (everything after the first +)
     */
    public abstract String getValue();

    /**
     * Validates the stateful portions of message (sequence numbers, transaction id) only
     */
    protected abstract void validateStateful() throws EdifactValidationException;

    /**
     * Validates non-stateful data items of the segment (excludes things like sequence numbers)
     */
    public abstract void preValidate() throws EdifactValidationException;

    public void validate() throws EdifactValidationException {
        this.preValidate();
        this.validateStateful();
    }

    public String toEdifact() throws EdifactValidationException {
        this.validate();
        return this.getKey() + "+" + this.getValue() + TERMINATOR;
    }

}
