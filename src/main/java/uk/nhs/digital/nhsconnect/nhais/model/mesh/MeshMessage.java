package uk.nhs.digital.nhsconnect.nhais.model.mesh;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MeshMessage {

    private String odsCode;
    private WorkflowId workflowId;
    private String content;
    /**
     * Correlation id associated with the request - used for distributed tracing
     */
    private String correlationId;
    /**
     * The timestamp (ISO format, UTC) when this message was sent - used for debugging and tracing
     */
    private String messageSentTimestamp;
    private String meshMessageId;
}
