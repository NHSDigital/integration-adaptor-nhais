package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    public Object getValue() {
        if (this.isExtension()) {
            return value;
        }
        else if (value != null) {
            return value.get();
        } else {
            return null;
        }
    }

    @JsonIgnore
    public AmendmentValue getAmendmentValue() {
        return value;
    }

    @JsonIgnore
    public String getFormattedSimpleValue() {
        if (isRemoval()) {
            return REMOVE_INDICATOR;
        }
        return value.get();
    }

    @JsonIgnore
    public String getNullSafeFormattedSimpleValue() {
        if (isRemoval()) {
            return REMOVE_INDICATOR;
        }
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return value.get();
    }

    @JsonIgnore
    public boolean isExtension() {
        return JsonPatches.EXTENSION_PATH.equalsIgnoreCase(path);
    }

    @JsonIgnore
    public boolean isNotExtension() {
        return !isExtension();
    }

    @JsonIgnore
    public boolean isRemoval() {
        return op == AmendmentPatchOperation.REMOVE;
    }
}