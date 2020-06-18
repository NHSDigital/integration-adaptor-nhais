package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

/**
 * Example FTX+RGI+++WRONG HA - TRY SURREY'
 */
@RequiredArgsConstructor
@Getter
public class FreeText extends Segment {
    private static final String KEY = "FTX";
    private static final String QUALIFIER = "RGI";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;

    private final String textLiteral;

    public static FreeText fromString(String edifactString) {
        if (!edifactString.startsWith(FreeText.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + FreeText.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = Split.byPlus(
            Split.bySegmentTerminator(edifactString)[0]
        );
        return new FreeText(split[4]);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return String.join(PLUS_SEPARATOR,
            QUALIFIER,
            StringUtils.EMPTY,
            StringUtils.EMPTY,
            textLiteral);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        // nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (StringUtils.isBlank(textLiteral)) {
            throw new EdifactValidationException(getKey() + ": Attribute textLiteral is required");
        }
    }
}
