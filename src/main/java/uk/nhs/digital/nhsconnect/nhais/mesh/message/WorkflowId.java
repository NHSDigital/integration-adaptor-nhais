package uk.nhs.digital.nhsconnect.nhais.mesh.message;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshWorkflowUnknownException;

import java.util.Arrays;

@RequiredArgsConstructor @Getter
public enum WorkflowId {
    REGISTRATION("NHAIS_REG"),
    RECEP("NHAIS_RECEP");

    @JsonValue
    private final String workflowId;

    @Override
    public String toString() {
        return workflowId;
    }

    public static WorkflowId fromString(String workflowId){
        return Arrays.stream(WorkflowId.values())
            .filter(workflow -> workflow.workflowId.equalsIgnoreCase(workflowId))
            .findFirst()
            .orElseThrow(() -> new MeshWorkflowUnknownException("Unsupported workflow id " + workflowId, workflowId));
    }
}
