package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AmendmentPatch {

    private AmendmentPatchOperation op;
    private String path;
    private String value;

}
