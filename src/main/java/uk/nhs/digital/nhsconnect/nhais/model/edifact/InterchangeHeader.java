package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A specialisation of a segment for the specific use case of an interchange header
 * takes in specific values required to generate an interchange header
 * example: UNB+UNOA:2+TES5+XX11+920113:1317+00000002'
 */
@Getter @Setter @RequiredArgsConstructor
public class InterchangeHeader extends Segment {

    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd:hhmm");

    private @NonNull String sender;
    private @NonNull String recipient;
    private @NonNull ZonedDateTime translationTime;
    private Integer sequenceNumber;

    @Override
    public String getKey() {
        return "UNB";
    }

    @Override
    public String getValue() {
        String timestamp = translationTime.format(DATE_FORMAT);
        return "asdf"+timestamp;
    }

    @Override
    protected void validateStateful() {
        // sequence number is > 1
    }

    @Override
    public void preValidate() {
        // sender, recipient are not empty
        // translationTime is not null
    }
}
