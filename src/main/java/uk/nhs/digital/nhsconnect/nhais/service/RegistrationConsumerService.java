package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

@Component @Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationConsumerService {

    private final InboundGpSystemService inboundGpSystemService;
    private final InboundStateRepository inboundStateRepository;
    private final OutboundStateRepository outboundStateRepository;
    private final OutboundMeshService outboundMeshService;
    private final RecepProducerService recepProducerService;

    public void handleRegistration(MeshMessage meshMessage) {
        LOGGER.debug("Received Registration message: {}", meshMessage);
        Interchange interchange = new EdifactParser().parse(meshMessage.getContent());
        LOGGER.debug("Parsed registration message into interchange: {}", interchange);

        var inboundState = InboundState.fromInterchange(interchange);
        if (!saveState(inboundState)) {
            return;
        }
        LOGGER.debug("Saved interchange in inbound state: {}", inboundState);

        var recep = recepProducerService.produceRecep(interchange);
        var outboundState = OutboundState.fromRecep(recep);
        outboundStateRepository.save(outboundState);
        LOGGER.debug("Saved recep in outbound state: {}", inboundState);

        Parameters outputParameters = new EdifactToFhirService().convertToFhir(interchange);
        LOGGER.debug("Converted registration message into FHIR: {}", outputParameters);
        inboundGpSystemService.publishToSupplierQueue(outputParameters, inboundState.getOperationId());
        LOGGER.debug("Published inbound registration message to gp supplier queue");

        var recepMeshMessage = new MeshMessage()
            //TODO: set other fields
            .setWorkflowId(WorkflowId.RECEP)
            .setContent(recep.toEdifact());
        outboundMeshService.send(recepMeshMessage);
        LOGGER.debug("Published recep to outbound queue");
    }

    private boolean saveState(InboundState inboundState) {
        try {
            inboundStateRepository.save(inboundState); // this can detect duplicates as there is an unique compound index
            LOGGER.debug("Saved inbound state: {}", inboundState);
            return true;
        } catch (DuplicateKeyException ex) {
            LOGGER.warn("Duplicate message received: {}", inboundState);
            return false;
        }
    }
}
