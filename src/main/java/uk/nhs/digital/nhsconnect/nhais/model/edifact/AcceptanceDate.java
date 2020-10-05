package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents the EDIFACT segment for the Acceptance Date data item
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class AcceptanceDate extends Segment {

    public static final String KEY = "DTM";
    public static final String TYPE_CODE = "956";
    public static final String FORMAT_CODE = "102";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final LocalDate acceptanceDate;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return TYPE_CODE + ":" + DATE_FORMAT.format(this.acceptanceDate) + ":" + FORMAT_CODE;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        // nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (acceptanceDate == null) {
            throw new EdifactValidationException(getKey() + ": Acceptance date is required");
        }
    }

}
