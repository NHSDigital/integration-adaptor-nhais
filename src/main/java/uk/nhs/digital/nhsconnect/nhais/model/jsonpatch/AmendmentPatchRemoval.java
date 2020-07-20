package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class AmendmentPatchRemoval extends AmendmentPatch {

    public AmendmentPatchRemoval(@NonNull String path) {
        super(AmendmentPatchOperation.REMOVE, path, null);
    }

    @JsonIgnore
    public AmendmentValue getValue() {
        return null;
    }

}
