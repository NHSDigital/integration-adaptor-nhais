package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

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

            var outboundState = new OutboundState()
                .setRecepCode(recepCode)
                .setRecepDateTime(dateTimePeriod);

            ReferenceMessageRecep.builder()
                .messageSequenceNumber(123L)
                .recepCode(ReferenceMessageRecep.RecepCode.CA)
                .build();

            outboundStateRepository.updateRecep(
                sender, recipient, interchangeSequence, messageSequence,
                recepCode, dateTimePeriod);
            // update outbound state from the recep using
            // sender + recipient + interchangeSequence + messageSequence
        }
    }
}
