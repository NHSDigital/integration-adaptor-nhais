package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AmendmentPatch {

    private AmendmentPatchOperation op;
    private String path;
    private AmendmentValue value;

}
