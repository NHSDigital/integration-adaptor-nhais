package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.RecepParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecepConsumerService {

    private final RecepParser recepParser;
    private final OutboundStateRepository outboundStateRepository;

    public void handleRecep(MeshMessage meshMessage) {
        LOGGER.info("Received RECEP message: {}", meshMessage);
        Recep recep = recepParser.parse(meshMessage.getContent());
        LOGGER.debug("Parsed RECEP message into: {}", recep);

        String sender = recep.getInterchangeHeader().getSender();
        String recipient = recep.getInterchangeHeader().getRecipient();
        Instant dateTimePeriod = recep.getDateTimePeriod().getTimestamp();
        long interchangeSequence = recep.getReferenceInterchangeRecep().getInterchangeSequenceNumber();

        for (ReferenceMessageRecep referenceMessageRecep : recep.getReferenceMessageReceps()) {
            long messageSequence = referenceMessageRecep.getMessageSequenceNumber();
            ReferenceMessageRecep.RecepCode recepCode = referenceMessageRecep.getRecepCode();

            //TODO could be done in transaction
            var queryParams = new OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams(
                sender, recipient, interchangeSequence, messageSequence);
            var recepDetails = new OutboundStateRepositoryExtensions.UpdateRecepDetails(
                recepCode, dateTimePeriod);

            var updatedOutboundState = outboundStateRepository.updateRecepDetails(queryParams, recepDetails);
            LOGGER.debug("Updated outbound state {} using query {} and recep details {}", updatedOutboundState, queryParams, recepDetails);
        }
    }
}
