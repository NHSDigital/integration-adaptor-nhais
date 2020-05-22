package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

@Component
public class EdifactToMeshMessageService {

    public MeshMessage toMeshMessage(TranslatedInterchange translatedInterchange) {
        // determine workflow id from message type: registration, recep
        // determine ODS code: probably via ENV?
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setOdsCode("ods123");
        meshMessage.setWorkflowId("workflow123");
        meshMessage.setInterchange(translatedInterchange.getEdifact());
        return meshMessage;
    }
}
