package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
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
        Interchange interchange;
        try {
            interchange = new EdifactParser().parse(meshMessage.getContent());
        } catch (ToEdifactParsingException ex) {
            createNackRecep(ex); //TODO implementation of that
            return;
        }
        LOGGER.debug("Parsed registration message into interchange: {}", interchange);

        var inboundState = InboundState.fromInterchange(interchange);
        if (!saveState(inboundState)) {
            return;
        }

        sendSequenceNumbersToInboundSequenceManager(inboundState); //TODO implementation of that

        // recep producer service
        Parameters outputParameters = new EdifactToFhirService().convertToFhir(interchange);
        LOGGER.debug("Converted registration message into FHIR: {}", outputParameters);
        inboundGpSystemService.publishToSupplierQueue(outputParameters, inboundState.getOperationId());
        LOGGER.debug("Published inbound registration message to gp supplier queue");
    }

    private void createNackRecep(ToEdifactParsingException ex) {
        //this exception should be passed to RECEP producer to create NACK RECEP
        LOGGER.error("Errors during parsing MESH message into EDIFACT", ex);
        throw ex;
    }

    private void sendSequenceNumbersToInboundSequenceManager(InboundState inboundState) {
        //sequence numbers for Inbound Sequence Number Manager can be extracted from InboundState
        LOGGER.debug("Interchange sequence number: " + inboundState.getReceiveInterchangeSequence());
        LOGGER.debug("Message sequence number: " + inboundState.getReceiveMessageSequence());
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
