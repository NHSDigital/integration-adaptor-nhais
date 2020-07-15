package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import javax.management.Query;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Example HEA+FRN+8:ZZZ'
 */
@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class FP69ReasonCode extends Segment {

    private final static List<Integer> ALLOWED_REASON_CODES = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private final static String KEY = "HEA";
    private final static String QUALIFIER = "FRN";
    public final static String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
    private final static String ZZZ_SUFFIX = ":ZZZ";

    private @NonNull Integer code;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER
            .concat(PLUS_SEPARATOR)
            .concat(code.toString())
            .concat(ZZZ_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (code == null) {
            throw new EdifactValidationException(getKey() + ": FP69 reason code is required");
        }
        if (ALLOWED_REASON_CODES.contains(code)) {
            throw new EdifactValidationException(getKey() + ": FP69 reason code illegal value");
        }
    }

    public static FP69ReasonCode fromString(String edifactString) {
        if (!edifactString.startsWith(FP69ReasonCode.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + FP69ReasonCode.class.getSimpleName() + " from " + edifactString);
        }
        var code = Split.byColon(Split.byPlus(edifactString)[2])[0];

        return new FP69ReasonCode(Integer.parseInt(code));
    }
}
