package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Arrays;

/**
 * Example: RFF+RIS:00000001 OK:4'
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ReferenceInterchangeRecep extends Segment {

    public static final String KEY = "RFF";
    public static final String QUALIFIER = "RIS";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;

    private final Long interchangeSequenceNumber;
    private final RecepCode recepCode;
    private final Integer messageCount;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return String.format("%s:%08d %s:%s", QUALIFIER, interchangeSequenceNumber, recepCode.getCode(), messageCount);
    }

    @Override
    protected void validateStateful() {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (interchangeSequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute messageSequenceNumber is required");
        }
        if (recepCode == null) {
            throw new EdifactValidationException(getKey() + ": Attribute recepCode is required");
        }
        if (messageCount == null) {
            throw new EdifactValidationException(getKey() + ": Attribute messageCount is required");
        }
    }

    public static ReferenceInterchangeRecep fromString(String edifactString) {
        if (!edifactString.startsWith(KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + ReferenceInterchangeRecep.class.getSimpleName() + " from " + edifactString);
        }
        String[] keySplit = edifactString.split("\\+");
        String[] sequenceWithCodeAndCount = keySplit[1].split("\\:");
        String[] sequenceWithCode = sequenceWithCodeAndCount[1].split("\\s");
        return new ReferenceInterchangeRecep(
            Long.parseLong(sequenceWithCode[0]),
            RecepCode.fromCode(sequenceWithCode[1]),
            Integer.parseInt(sequenceWithCodeAndCount[2]));
    }

    @Getter
    @RequiredArgsConstructor
    public enum RecepCode {
        RECEIVED("OK", "Received successfully"),
        NO_VALID_DATA("NA", "No valid data in interchange"),
        INVALID_DATA("ER", "Valid with invalid data in interchange");

        private final String code;
        private final String description;

        public static RecepCode fromCode(@NonNull String code) {
            return Arrays.stream(RecepCode.values())
                .filter(rc -> code.equals(rc.getCode()))
                .findFirst()
                .orElseThrow();
        }
    }
}
