package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

/**
 *class declaration:
 */
@Getter @Setter @RequiredArgsConstructor
public class NameAndAddress extends Segment{

    public static final String KEY = "NAD";
    private @NonNull String identifier;
    private @NonNull QualifierAndCode partyQualifierAndCode;

    public enum QualifierAndCode {
        FHS("FHS", "954");

        QualifierAndCode(String qualifier, String code) {
            this.code = code;
            this.qualifier = qualifier;
        }

        private final String code;
        private final String qualifier;

        public String getCode() {
            return this.code;
        }

        public String getQualifier(){
            return this.qualifier;
        }
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
        if(partyQualifierAndCode.getQualifier().isEmpty()){
            throw new EdifactValidationException(getKey() + ": Attribute qualifier is required");
        }
    }

    public static NameAndAddress fromString(String edifactString) {
        return null;
    }
}
