package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.RecepParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecepConsumerService {

    private final RecepParser recepParser;
    private final OutboundStateRepository outboundStateRepository;
    private final InboundStateRepository inboundStateRepository;

    public void handleRecep(MeshMessage meshMessage) {
        LOGGER.info("Received RECEP message: {}", meshMessage);
        Recep recep = recepParser.parse(meshMessage.getContent());
        LOGGER.debug("Parsed RECEP message into: {}", recep);

        var inboundState = InboundState.fromRecep(recep);
        if (!saveState(inboundState)) {
            return;
        }

        //sender is swapped with recipient as communication is done the opposite way
        String outbound_sender = recep.getInterchangeHeader().getRecipient();
        String outbound_recipient = recep.getInterchangeHeader().getSender();
        Instant dateTimePeriod = recep.getDateTimePeriod().getTimestamp();
        long interchangeSequence = recep.getReferenceInterchangeRecep().getInterchangeSequenceNumber();

        for (ReferenceMessageRecep referenceMessageRecep : recep.getReferenceMessageReceps()) {
            long messageSequence = referenceMessageRecep.getMessageSequenceNumber();
            ReferenceMessageRecep.RecepCode recepCode = referenceMessageRecep.getRecepCode();

            //TODO could be done in transaction
            var queryParams = new OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams(
                outbound_sender, outbound_recipient, interchangeSequence, messageSequence);
            var recepDetails = new OutboundStateRepositoryExtensions.UpdateRecepDetails(
                recepCode, dateTimePeriod);

            var updatedOutboundState = outboundStateRepository.updateRecepDetails(queryParams, recepDetails);
            LOGGER.debug("Updated outbound state {} using query {} and recep details {}", updatedOutboundState, queryParams, recepDetails);
        }
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
