package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshCypherDecoder;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EdifactToMeshMessageService {

    private final TimestampService timestampService;

    private final MeshCypherDecoder meshCypherDecoder;

    private final EdifactParser edifactParser;

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
        meshMessage.setMessageSentTimestamp(timestampService.formatInISO(timestampService.getCurrentTimestamp()));
        return meshMessage;
    }

    public MeshMessage fromEdifactString(String edifactString, String messageId) {
        Interchange interchange = edifactParser.parse(edifactString);
        //send to inbound queue
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setContent(edifactString);
        meshMessage.setOdsCode(meshCypherDecoder.getSender(edifactString));
        meshMessage.setWorkflowId(RecepMessage.isRecep(edifactString) ? WorkflowId.RECEP : WorkflowId.REGISTRATION);
        meshMessage.setCorrelationId(UUID.randomUUID().toString()); //TODO: implement correlation id handling
        meshMessage.setMeshMessageId(messageId);
        meshMessage.setMessageSentTimestamp(timestampService.formatInISO(interchange.getInterchangeHeader().getTranslationTime()));
        return meshMessage;
    }
}
