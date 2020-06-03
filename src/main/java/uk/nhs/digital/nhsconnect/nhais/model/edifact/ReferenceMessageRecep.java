package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter
@Setter
@RequiredArgsConstructor
public class ReferenceMessageRecep extends Segment {

    public static final String KEY = "RFF";
    public static final String QUALIFIER = "MIS";

    private final Long messageSequenceNumber;
    private final RecepCode recepCode;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return QUALIFIER + ":" + messageSequenceNumber + " " + recepCode.getCode();
    }

    @Override
    protected void validateStateful() {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (messageSequenceNumber == null) {
            throw new EdifactValidationException(getKey() + ": Attribute messageSequenceNumber is required");
        }
        if (recepCode == null) {
            throw new EdifactValidationException(getKey() + ": Attribute recepCode is required");
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum RecepCode {
        SUCCESS("CP", "Translation successful"),
        ERROR("CA", "Translation error"),
        INCOMPLETE("CI", "Translation incomplete due to a fatal error during translation");

        private final String code;
        private final String description;
    }
}
