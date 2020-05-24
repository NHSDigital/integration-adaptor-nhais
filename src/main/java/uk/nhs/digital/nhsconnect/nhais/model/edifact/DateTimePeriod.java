package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *class declaration:
 */
@Getter @Setter @RequiredArgsConstructor
public class DateTimePeriod extends Segment{

    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd:hhmm");

    private @NonNull ZonedDateTime timestamp;
    private @NonNull String typeCode;
    private @NonNull String formatCode;
    private @NonNull String dateTimeFormat;

    //enum class
//    TRANSLATION_TIMESTAMP = ('137', '203', '%Y%m%d%H%M')
//    PERIOD_END_DATE = ('206', '102', '%Y%m%d')

    @Override
    public String getKey() {
        return "DTM";
    }

    @Override
    public String getValue() {
        String formattedTimestamp = this.timestamp.format(DATE_FORMAT); //might need to accept format in pramas
        return typeCode + ":" + formattedTimestamp + ":" + formatCode;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (typeCode.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute typeCode is required");
        }
        if(formatCode.isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute formatCode is required");
        }
        if (dateTimeFormat.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute dateTimeFormat is required");
        }
    }
}
