package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;

@Component @Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationConsumerService {

    private final InboundGpSystemService inboundGpSystemService;
    private final InboundStateRepository inboundStateRepository;

    public void handleRegistration(MeshMessage meshMessage) {
        LOGGER.debug("Received Registration message: {}", meshMessage);
        Interchange interchange = new EdifactParser().parse(meshMessage.getContent());
        LOGGER.debug("Parsed registration message into interchange: {}", interchange);

        var inboundState = InboundState.fromInterchange(interchange);
        if (!saveState(inboundState)) {
            return;
        }

        // recep producer service
        Parameters outputParameters = new EdifactToFhirService().convertToFhir(interchange);
        LOGGER.debug("Converted registration message into FHIR: {}", outputParameters);
        inboundGpSystemService.publishToSupplierQueue(outputParameters, inboundState.getOperationId());
        LOGGER.debug("Published inbound registration message to gp supplier queue");
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
