package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter
@Setter
public class ReferenceMessageRecep extends Reference {
    private Long messageSequenceNumber;
    private RecepCode recepCode;

    public ReferenceMessageRecep(@NonNull Long messageSequenceNumber, @NonNull RecepCode recepCode) {
        super("MIS", buildReferenceString(messageSequenceNumber, recepCode));
        this.messageSequenceNumber = messageSequenceNumber;
        this.recepCode = recepCode;
    }

    public void setMessageSequenceNumber(@NonNull Long messageSequenceNumber) {
        this.messageSequenceNumber = messageSequenceNumber;
        this.setReference(buildReferenceString());
    }

    public void setRecepCode(@NonNull RecepCode recepCode) {
        this.recepCode = recepCode;
        this.setReference(buildReferenceString());
    }

    @Override
    public Reference setReference(@NonNull String reference) {
        throw new IllegalAccessError("Use setReferenceId and setRecepCode to build reference");
    }

    private static String buildReferenceString(Long messageSequenceNumber, RecepCode recepCode) {
        return messageSequenceNumber + " " + recepCode.name();
    }

    private String buildReferenceString() {
        return buildReferenceString(this.messageSequenceNumber, this.recepCode);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (StringUtils.isEmpty(getReference())) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }

    @Getter
    public enum RecepCode {
        CP("CP", "Translation successful"),
        CA("CA", "Translation error"),
        CI("CI", "Translation incomplete due to a fatal error during translation");

        private final String code;
        private final String description;

        RecepCode(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
