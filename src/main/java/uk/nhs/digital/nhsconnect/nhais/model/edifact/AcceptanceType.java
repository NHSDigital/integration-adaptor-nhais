package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

@Builder
@Data
public class AcceptanceType extends Segment {
    private final static String KEY = "HEA";
    private final static String APT_PREFIX = "ATP";
    private final static String ZZZ_SUFFIX = ":ZZZ";

    private @NonNull AvailableTypes acceptanceType;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return APT_PREFIX
            .concat(PLUS_SEPARATOR)
            .concat(acceptanceType.getCode())
            .concat(ZZZ_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(acceptanceType)) {
            throw new EdifactValidationException(getKey() + ": Acceptance Type is required");
        }
    }

    public enum AvailableTypes {
        BIRTH("1"),
        FIRST("2"),
        TRANSFER_IN("3"),
        IMMIGRANT("4");

        @Getter
        public String code;
        AvailableTypes(String code) {
            this.code = code;
        }

        public static AvailableTypes fromCode(String code) {
            return Arrays.stream(AvailableTypes.values())
                .filter(acceptanceType -> acceptanceType.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("acceptanceType element not found"));
        }
    }
}
