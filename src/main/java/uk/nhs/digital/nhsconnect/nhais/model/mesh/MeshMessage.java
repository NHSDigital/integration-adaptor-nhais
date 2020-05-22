package uk.nhs.digital.nhsconnect.nhais.model.mesh;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MeshMessage {

    private String odsCode;
    private String workflowId;
    private String interchange;

}
