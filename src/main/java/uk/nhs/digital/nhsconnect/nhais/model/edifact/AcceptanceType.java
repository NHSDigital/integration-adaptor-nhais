package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

@Builder
@Data
public class AcceptanceType extends Segment {
    private final static String KEY = "HEA";
    private final static String APT_PREFIX = "ATP";
    private final static String ZZZ_SUFFIX = ":ZZZ";

    private @NonNull String type;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return APT_PREFIX
            .concat(PLUS_SEPARATOR)
            .concat(type)
            .concat(ZZZ_SUFFIX);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(type) || type.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Acceptance Type is required");
        }

        if (!AcceptanceTypes.isValidCode(type)) {
            throw new EdifactValidationException(getKey() + "Acceptance Type not allowed: " + type);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum AcceptanceTypes {
        BIRTH("birth", "1"),
        FIRST("first", "2"),
        TRANSFERIN("transferin", "3"),
        IMMIGRANT("immigrant", "4");

        final String value;
        final String code;

        public static String toCode(String fromValue) {
            return Arrays.stream(AcceptanceTypes.values())
                .filter(type -> type.getValue().equals(fromValue))
                .findFirst()
                .map(AcceptanceTypes::getCode)
                .orElseThrow(() -> new NoSuchElementException("acceptanceType element not found"));
        }

        public static boolean isValidCode(String inputCode) {
            return Arrays.stream(AcceptanceTypes.values())
                .anyMatch(type -> type.getCode().equals(inputCode));
        }
    }
}
