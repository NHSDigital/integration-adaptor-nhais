package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.Value;

@Value
public class AmendmentPatch {

    AmendmentPatchOperation op;
    String path;
    AmendmentValue value;

}
