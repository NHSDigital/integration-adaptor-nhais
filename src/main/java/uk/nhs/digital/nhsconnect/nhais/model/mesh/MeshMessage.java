package uk.nhs.digital.nhsconnect.nhais.model.mesh;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MeshMessage implements InboundMeshMessage, OutboundMeshMessage {

    /**
     * If SENDING TO MESH: set to the HA Trading Partner Code (recipient)
     * If DOWNLOADING FROM MESH: DO NOT USE
     */
    private String haTradingPartnerCode;

    /**
     * If SENDING TO MESH: set based on the type of transaction being sent
     * If DOWNLOADING FROM MESH: set to the Mex-WorkflowID response header
     */
    private WorkflowId workflowId;

    /**
     * If SENDING TO MESH: the content of the MESH message
     * If DOWNLOADING FROM MESH: the body of the download response
     */
    private String content;

    /**
     * If SENDING TO MESH: the time this message was published to the outbound mesh MQ
     * If DOWNLOADING FROM MESH: the time this message was published to the inbound mesh MQ
     */
    private String messageSentTimestamp;

    /**
     * If SENDING TO MESH: DO NOT USE
     * If DOWNLOADING FROM MESH: the message id of the message that was downloaded from MESH
     */
    private String meshMessageId;

    /**
     * If SENDING TO MESH: Value of OperationId response header for GP System
     * If DOWNLOADING FROM MESH: DO NOT USE
     */
    private String operationId;

}
