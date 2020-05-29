package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.*;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This segment is used to provide a code for the message which indicates its use. It is a constant of EDIFACT
 * example: BGM+++507'
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BeginningOfMessage extends Segment {

    private static final Pattern BGM_PATTERN = Pattern.compile("BGM\\+\\+\\+507'");

    public static BeginningOfMessage fromEdifact(String edifactFile) {
        Matcher match = BGM_PATTERN.matcher(edifactFile);
        if(match.find()) {
            return new BeginningOfMessage();
        }
        throw new EdifactValidationException("Unable to parse BGM (Beginning of Message) segment");
    }

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
