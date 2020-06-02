package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Getter @Setter @RequiredArgsConstructor
public class DateTimePeriod extends Segment{

    private @NonNull Instant timestamp;
    private @NonNull TypeAndFormat typeAndFormat;

    public enum TypeAndFormat {
        TRANSLATION_TIMESTAMP("137", "203", "yyyyMMddHHmm");

        private final String typeCode;
        private final String formatCode;
        private final String dateTimeFormat;

        TypeAndFormat(String typeCode, String formatCode, String dateTimeFormat) {
            this.typeCode = typeCode;
            this.formatCode = formatCode;
            this.dateTimeFormat = dateTimeFormat;
        }

        public String getTypeCode() {
            return this.typeCode;
        }

        public String getFormatCode(){
            return this.formatCode;
        }

        public DateTimeFormatter getDateTimeFormat(){
            return DateTimeFormatter.ofPattern(this.dateTimeFormat)
                .withZone(TimestampService.UKZone);
        }
    }

    @Override
    public String getKey() {
        return "DTM";
    }

    @Override
    public String getValue() {
        String formattedTimestamp = typeAndFormat.getDateTimeFormat().format(this.timestamp);
        return typeAndFormat.getTypeCode() + ":" + formattedTimestamp + ":" + typeAndFormat.getFormatCode();
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
//        if (typeCode.isEmpty()) {
//            throw new EdifactValidationException(getKey() + ": Attribute typeCode is required");
//        }
//        if(formatCode.isEmpty()){
//            throw new EdifactValidationException(getKey() + ": Attribute formatCode is required");
//        }
//        if (dateTimeFormat.isEmpty()) {
//            throw new EdifactValidationException(getKey() + ": Attribute dateTimeFormat is required");
//        }
    }
}
