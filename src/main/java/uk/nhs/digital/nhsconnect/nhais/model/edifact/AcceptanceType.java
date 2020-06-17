package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Builder
@Data
public class AcceptanceType extends Segment {
    private final static String KEY = "HEA";
    private final static String APT_PREFIX = "ATP";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    private final static Map<String, String> ACC_TYPE_MAPPING = ImmutableMap.of(
        AcceptanceTypes.BIRTH.toString(), "1",
        AcceptanceTypes.FIRST.toString(), "2",
        AcceptanceTypes.TRANSFERIN.toString(), "3",
        AcceptanceTypes.IMMIGRANT.toString(), "4"
    );
    private @NonNull String type;

    public static String getTypeValue(String input) {
        return Optional.ofNullable(ACC_TYPE_MAPPING.get(input))
            .orElseThrow(() -> new NoSuchElementException("acceptanceType element not found"));
    }

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

        if (!ACC_TYPE_MAPPING.containsValue(type)) {
            throw new EdifactValidationException(getKey() + "Acceptance Type not allowed: " + type);
        }
    }

    private enum AcceptanceTypes {
        BIRTH("birth"),
        FIRST("first"),
        TRANSFERIN("transferin"),
        IMMIGRANT("immigrant");

        String type;

        AcceptanceTypes(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
