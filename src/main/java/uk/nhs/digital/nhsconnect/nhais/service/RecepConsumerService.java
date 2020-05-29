package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;

@Component
@Slf4j
public class RecepConsumerService {

    private final EdifactParser edifactParser;
    private final OutboundStateRepository outboundStateRepository;

    @Autowired
    public RecepConsumerService(EdifactParser edifactParser, OutboundStateRepository outboundStateRepository) {
        this.edifactParser = edifactParser;
        this.outboundStateRepository = outboundStateRepository;
    }

    public void handleRecep(MeshMessage meshMessage) {
        LOGGER.info("Received RECEP message: {}", meshMessage);
        Interchange interchange = edifactParser.parse(meshMessage.getContent());
        LOGGER.debug("Parsed registration message into interchange: {}", interchange);

        String sender = interchange.getInterchangeHeader().getSender();
        String recipient = interchange.getInterchangeHeader().getRecipient();
        Instant dateTimePeriod = interchange.getDateTimePeriod().getTimestamp();
        long interchangeSequence = interchange.getInterchangeHeader().getSequenceNumber();

        for (ReferenceMessageRecep referenceMessageRecep : interchange.getReferenceMessageReceps()) {
            long messageSequence = referenceMessageRecep.getMessageSequenceNumber();
            ReferenceMessageRecep.RecepCode recepCode = referenceMessageRecep.getRecepCode();

            //TODO could be done in transaction
            outboundStateRepository.updateRecepDetails(
                new OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams(
                    sender, recipient, interchangeSequence, messageSequence),
                new OutboundStateRepositoryExtensions.UpdateRecepDetails(
                    recepCode, dateTimePeriod));
        }
    }
}
