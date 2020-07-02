package uk.nhs.digital.nhsconnect.nhais.model.mesh;

public interface OutboundMeshMessage {
    String getHaTradingPartnerCode();
    WorkflowId getWorkflowId();
    String getContent();
    String getMessageSentTimestamp();
    OutboundMeshMessage setMessageSentTimestamp(String timestamp);
    String getOperationId();
}
