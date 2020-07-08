package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class Segment {
    protected static final String PLUS_SEPARATOR = "+";
    protected final static String COLON_SEPARATOR = ":";
    private static final String TERMINATOR = "'";

    protected static <T> List<T> removeEmptyTrailingFields(List<T> list, Predicate<T> predicate) {
        var result = new ArrayList<T>();
        Collections.reverse(list);
        boolean foundFirstValid = false;
        for (T element : list) {
            if (foundFirstValid || predicate.test(element)) {
                foundFirstValid = true;
                result.add(element);
            }
        }
        Collections.reverse(result);
        return result;
    }

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
        return this.getKey() + PLUS_SEPARATOR + this.getValue() + TERMINATOR;
    }
}
