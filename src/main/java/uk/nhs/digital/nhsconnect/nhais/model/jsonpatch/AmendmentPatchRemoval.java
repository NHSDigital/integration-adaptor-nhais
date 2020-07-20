package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class AmendmentPatchRemoval extends AmendmentPatch {

    @NonNull
    private final AmendmentPatchOperation op = AmendmentPatchOperation.REMOVE;
    @NonNull
    private String path;
    @JsonIgnore
    private AmendmentValue value;
}
