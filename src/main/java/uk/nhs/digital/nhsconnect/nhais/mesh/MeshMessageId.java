package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
public class MeshMessageId {
    private final String messageID;

    @JsonCreator
    public MeshMessageId(@JsonProperty("messageId") String messageID) {
        this.messageID = messageID;
    }
}
