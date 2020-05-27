package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

@Component
public class EdifactToMeshMessageService {

    public MeshMessage toMeshMessage(TranslatedInterchange translatedInterchange) {
        // TODO: determine ODS code: probably via ENV?
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setOdsCode("ods123");
        switch(translatedInterchange.getInterchangeType()) {
            case REGISTRATION:
                meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
                break;
            case RECEP:
                meshMessage.setWorkflowId(WorkflowId.RECEP);
                break;
        }
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setContent(translatedInterchange.getEdifact());
        return meshMessage;
    }
}
