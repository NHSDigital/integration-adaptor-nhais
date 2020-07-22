package uk.nhs.digital.nhsconnect.nhais.mesh.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface OutboundMeshMessage {
    String getHaTradingPartnerCode();
    WorkflowId getWorkflowId();
    String getContent();
    String getMessageSentTimestamp();
    OutboundMeshMessage setMessageSentTimestamp(String timestamp);
    String getOperationId();

    @JsonCreator
    static OutboundMeshMessage create(
            @JsonProperty(value = "haTradingPartnerCode") String haTradingPartnerCode,
            @JsonProperty(value = "workflowId") WorkflowId workflowId,
            @JsonProperty(value = "content") String content,
            @JsonProperty(value = "messageSentTimestamp") String messageSentTimestamp,
            @JsonProperty(value = "operationId") String operationId
    ) {
        return new MeshMessage()
                .setWorkflowId(workflowId)
                .setContent(content)
                .setMessageSentTimestamp(messageSentTimestamp)
                .setHaTradingPartnerCode(haTradingPartnerCode)
                .setOperationId(operationId);
    }
}
