package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReferenceMessageRecep extends Reference {

    public static final String QUALIFIER = "MIS";

    private @NonNull Long messageSequenceNumber;
    private @NonNull RecepCode recepCode;

    private String buildReferenceString() {
        return messageSequenceNumber + " " + recepCode.name();
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        if (StringUtils.isEmpty(getReference())) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }

    @Override
    protected String getReference() {
        return buildReferenceString();
    }

    @Override
    protected String getQualifier() {
        return QUALIFIER;
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
