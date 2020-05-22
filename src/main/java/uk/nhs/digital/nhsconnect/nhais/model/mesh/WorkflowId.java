package uk.nhs.digital.nhsconnect.nhais.model.mesh;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
