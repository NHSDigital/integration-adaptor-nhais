package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Arrays;

/**
 * example: NAD+FHS+HA456:954'
 */
@Getter
@Setter
@RequiredArgsConstructor
public class NameAndAddress extends Segment {

    public static final String KEY = "NAD";
    private @NonNull String identifier;
    private @NonNull QualifierAndCode partyQualifierAndCode;

    public static NameAndAddress fromString(String edifactString) {
        if (!edifactString.startsWith(NameAndAddress.KEY)) {
            throw new IllegalArgumentException("Can't create " + NameAndAddress.class.getSimpleName() + " from " + edifactString);
        }
        String[] split = edifactString.split("'")[0].split("\\+");

        var qualifierAndCode = Arrays.stream(QualifierAndCode.values())
            .filter(qc -> qc.getCode().equals(split[2].split(":")[1]) && qc.getQualifier().equals(split[1]))
            .findFirst()
            .orElseThrow();

        return new NameAndAddress(split[2].split(":")[0], qualifierAndCode);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return partyQualifierAndCode.getQualifier() + "+" + identifier + ":" + partyQualifierAndCode.getCode();
    }

    @Override
    protected void validateStateful() {
        // Do nothing
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (identifier.isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
        //Is below needed as it will always be populated
        if (partyQualifierAndCode.getCode().isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute code is required");
        }
        if (partyQualifierAndCode.getQualifier().isEmpty()) {
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }

    public enum QualifierAndCode {
        FHS("FHS", "954");

        private final String code;
        private final String qualifier;
        QualifierAndCode(String qualifier, String code) {
            this.code = code;
            this.qualifier = qualifier;
        }

        public String getCode() {
            return this.code;
        }

        public String getQualifier() {
            return this.qualifier;
        }
    }
}
