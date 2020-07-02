package uk.nhs.digital.nhsconnect.nhais.model.mesh;

public interface InboundMeshMessage {
    WorkflowId getWorkflowId();
    String getContent();
    String getMessageSentTimestamp();
    InboundMeshMessage setMessageSentTimestamp(String timestamp);
    String getMeshMessageId();
}
