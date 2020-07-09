package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.logging.log4j.util.Strings;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AmendmentPatch {

    private static final String REMOVE_INDICATOR = "%";

    private AmendmentPatchOperation op;
    private String path;
    private AmendmentValue value;

    public String getFormattedSimpleValue() {
        if (op == AmendmentPatchOperation.REMOVE) {
            return REMOVE_INDICATOR;
        }
        return value.get();
    }

    public String getNullableFormattedSimpleValue() {
        if (op == AmendmentPatchOperation.REMOVE) {
            return REMOVE_INDICATOR;
        }
        if (value == null) {
            return Strings.EMPTY;
        }
        return value.get();
    }

    public boolean isRemoval() {
        return op == AmendmentPatchOperation.REMOVE;
    }
}