package uk.nhs.digital.nhsconnect.nhais.mesh;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MeshClient {

    // TODO: stub, to be replaced by NIAD-266
    public void sendEdifactMessage(String messageContent, String recipient) {

    }

    // TODO: stub, to be replaced by NIAD-265
    public List<String> getInboxMessageIds() {
        return Collections.emptyList();
    }

    // TODO: stub, to be replaced by NIAD-266
    public String getMessage(String messageId) {
        return "the message";
    }

}
