package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AmendmentPatch {

    public static final String REMOVE_INDICATOR = "%";

    @NonNull
    private AmendmentPatchOperation op;
    @NonNull
    private String path;
    private AmendmentValue value;

    public String getFormattedSimpleValue() {
        if (op == AmendmentPatchOperation.REMOVE) {
            return REMOVE_INDICATOR;
        }
        return value.get();
    }

    public String getNullSafeFormattedSimpleValue() {
        if (op == AmendmentPatchOperation.REMOVE) {
            return REMOVE_INDICATOR;
        }
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return value.get();
    }

    public boolean isExtension() {
        return "/extension/0".equalsIgnoreCase(path);
    }

    public boolean isNotExtension() {
        return !isExtension();
    }

    public boolean isRemoval() {
        return op == AmendmentPatchOperation.REMOVE;
    }
}