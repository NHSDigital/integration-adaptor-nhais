package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class DeductionDate extends Segment{

    public static final String KEY = "DTM";
    private static final String TYPE_CODE = "961";
    private static final String FORMAT_CODE = "102";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyMMdd");

    @NonNull
    private final LocalDate date;

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
