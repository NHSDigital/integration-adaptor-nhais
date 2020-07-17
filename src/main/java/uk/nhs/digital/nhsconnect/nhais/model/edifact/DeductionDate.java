package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Example DTM+961:19920125:102'
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false) @ToString
public class DeductionDate extends Segment{

    public static final String KEY = "DTM";
    private static final String TYPE_CODE = "961";
    public static final String KEY_QUALIFIER = KEY + "+" + TYPE_CODE;
    private static final String FORMAT_CODE = "102";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyMMdd");

    @NonNull
    private final LocalDate date;

    public static DeductionDate fromString(String edifactString) {
        if (!edifactString.startsWith(DeductionDate.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + DeductionDate.class.getSimpleName() + " from " + edifactString);
        }
        String[] keySplit = Split.byPlus(edifactString);
        String deductionDate = Split.byColon(keySplit[1])[1];
        return new DeductionDate(LocalDate.parse(deductionDate, FORMAT));
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        String formattedTimestamp = FORMAT.format(this.date);
        return TYPE_CODE + ":" + formattedTimestamp + ":" + FORMAT_CODE;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (date == null) {
            throw new EdifactValidationException(getKey() + ": Attribute timestamp is required");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        // nothing
    }
}
