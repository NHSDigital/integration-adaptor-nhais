package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *class declaration:
 */
@Getter @Setter @RequiredArgsConstructor
public class DateTimePeriod extends Segment{

    private static final Pattern PATTERN = Pattern.compile("DTM\\+(?<typeCode>137|206):(?<dateTimeValue>[0-9]{12}|[0-9]{8}):(?<formatCode>203|102)'");

    private @NonNull ZonedDateTime timestamp;
    private @NonNull TypeAndFormat typeAndFormat;

    public static DateTimePeriod fromEdifact(String edifactFile) {
        Matcher matcher = PATTERN.matcher(edifactFile);
        if(matcher.find()) {
            String typeCode = matcher.group("typeCode");
            String dateTimeValue = matcher.group("dateTimeValue");
            TypeAndFormat typeAndFormat = TypeAndFormat.getByTypeCode(typeCode);
            ZonedDateTime timestamp = ZonedDateTime.parse(dateTimeValue, DateTimeFormatter.ofPattern(typeAndFormat.dateTimeFormat));
            return new DateTimePeriod(timestamp, typeAndFormat);
        }
        throw new EdifactValidationException("Unable to parse DTM (Date Time Period) segment");
    }

    public enum TypeAndFormat {
        TRANSLATION_TIMESTAMP("137", "203", "yyyyMMddHHmm");

        private static final Map<String, TypeAndFormat> BY_TYPE_CODE = Arrays.stream(TypeAndFormat.values())
                .collect(Collectors.toMap(TypeAndFormat::getTypeCode, Function.identity()));

        private final String typeCode;
        private final String formatCode;
        private final String dateTimeFormat;

        TypeAndFormat(String typeCode, String formatCode, String dateTimeFormat) {
            this.typeCode = typeCode;
            this.formatCode = formatCode;
            this.dateTimeFormat = dateTimeFormat;
        }

        public static TypeAndFormat getByTypeCode(String typeCode) {
            return BY_TYPE_CODE.get(typeCode);
        }

        public String getTypeCode() {
            return this.typeCode;
        }

        public String getFormatCode(){
            return this.formatCode;
        }

        public DateTimeFormatter getDateTimeFormat(){
            return DateTimeFormatter.ofPattern(this.dateTimeFormat);
        }
    }

    @Override
    public String getKey() {
        return "DTM";
    }

    @Override
    public String getValue() {
        String formattedTimestamp = this.timestamp.format(typeAndFormat.getDateTimeFormat());
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
