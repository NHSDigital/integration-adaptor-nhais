package uk.nhs.digital.nhsconnect.nhais;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.inbound.RegistrationConsumer;
import uk.nhs.digital.nhsconnect.nhais.inbound.RecepProducerService;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecepResponderService implements RegistrationConsumer {

    private final OutboundQueueService outboundQueueService;
    private final RecepProducerService recepProducerService;
    private final EdifactParser edifactParser;

    @Override
    public void handleRegistration(InboundMeshMessage meshMessage) {
        LOGGER.debug("Received Registration message: {}", meshMessage);
        Interchange interchange = edifactParser.parse(meshMessage.getContent());

        var recepEdifact = recepProducerService.produceRecep(interchange);
        var recep = edifactParser.parse(recepEdifact);
        var recepOutboundMessage = prepareRecepOutboundMessage(recepEdifact, recep);

        outboundQueueService.publish(recepOutboundMessage);

    }

    private OutboundMeshMessage prepareRecepOutboundMessage(String recepEdifact, Interchange recep) {
        var recepMeshMessage = buildRecepMeshMessage(recepEdifact, recep);
        LOGGER.debug("Wrapped recep in mesh message: {}", recepMeshMessage);
        return recepMeshMessage;
    }

    private OutboundMeshMessage buildRecepMeshMessage(String edifactRecep, Interchange recep) {
        return new MeshMessage()
            .setHaTradingPartnerCode(recep.getInterchangeHeader().getRecipient())
            .setWorkflowId(WorkflowId.RECEP)
            .setContent(edifactRecep);
    }
}
