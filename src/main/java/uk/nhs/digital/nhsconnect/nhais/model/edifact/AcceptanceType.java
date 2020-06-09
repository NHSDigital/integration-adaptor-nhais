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
    private final static String APT_PREFIX = "ATP";
    private final static String ZZZ_SUFFIX = ":ZZZ";
    private final static Map<String, String> ACC_TYPE_MAPPING = ImmutableMap.of(
            "birth", "1",
            "first", "2",
            "transferin", "3",
            "immigrant", "4",
            "exservices", "5"
    );
    private @NonNull String type;

    public static String getTypeValue(String input) {
        return Optional.ofNullable(ACC_TYPE_MAPPING.get(input))
                .orElseThrow(() -> new NoSuchElementException("acceptanceType element not found"));
    }

    @Override
    public String getKey() {
        return "HEA";
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
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }

        if (!ACC_TYPE_MAPPING.containsValue(type)) {
            throw new EdifactValidationException(getKey() + "Acceptance Type not allowed: " + type);
        }
    }
}
