package uk.nhs.digital.nhsconnect.nhais.mesh.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MeshMessages {
    private final String[] messageIDs;

    @JsonCreator
    public MeshMessages(@JsonProperty("messages") String[] messageIDs) {
        this.messageIDs = messageIDs;
    }
}
