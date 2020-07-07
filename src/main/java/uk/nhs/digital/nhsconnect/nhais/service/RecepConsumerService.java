package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecepConsumerService {

    private final EdifactParser edifactParser;
    private final OutboundStateRepository outboundStateRepository;
    private final InboundStateRepository inboundStateRepository;

    public void handleRecep(InboundMeshMessage meshMessage) {
        LOGGER.info("Received RECEP message: {}", meshMessage);
        var recep = edifactParser.parse(meshMessage.getContent());
        LOGGER.debug("Parsed RECEP message into: {}", recep);

        var messagesToProcess = filterOutDuplicates(recep);

        var outboundStateUpdates = prepareOutboundStateUpdates(messagesToProcess);
        var inboundStateInserts = prepareInboundStateInserts(messagesToProcess);

        updateOutboundStateWithRecepDetails(outboundStateUpdates);
        insertInboundState(inboundStateInserts);
    }

    private void insertInboundState(List<InboundState> inboundStateInserts) {
        LOGGER.debug("Inserting InboundState records {}", inboundStateInserts);
        inboundStateInserts.forEach(inboundStateRepository::save);
    }

    private void updateOutboundStateWithRecepDetails(List<OutboundStateRepositoryExtensions.UpdateRecepParams> updateRecepParams) {
        LOGGER.debug("Updating OutboundState with recep details {}", updateRecepParams);
        updateRecepParams.forEach(updateRecepParam -> {
            outboundStateRepository.updateRecepDetails(updateRecepParam).ifPresentOrElse(
                outboundState -> LOGGER.debug("Updated outbound state recep details using {}", updateRecepParams),
                () -> LOGGER.warn("No outbound state row was updated with recep details using {}", updateRecepParams)
            );
        });
    }

    private List<InboundState> prepareInboundStateInserts(List<Message> recepMessages) {
        return recepMessages.stream()
            .map(InboundState::fromRecep)
            .collect(Collectors.toList());
    }

    private List<OutboundStateRepositoryExtensions.UpdateRecepParams> prepareOutboundStateUpdates(List<Message> messagesToProcess) {
        return messagesToProcess.stream()
            .map(this::prepareOutboundStateUpdateParams)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<OutboundStateRepositoryExtensions.UpdateRecepParams> prepareOutboundStateUpdateParams(Message message) {
        //sender is swapped with recipient as communication is done the opposite way
        var outbound_sender = message.getInterchange().getInterchangeHeader().getRecipient();
        var outbound_recipient = message.getInterchange().getInterchangeHeader().getSender();
        var dateTimePeriod = message.getTranslationDateTime().getTimestamp();
        var interchangeSequence = message.getReferenceInterchangeRecep().getInterchangeSequenceNumber();

        return message.getReferenceMessageReceps().stream()
            .map(referenceMessageRecep -> prepareOutboundStateUpdateParams(outbound_sender, outbound_recipient, dateTimePeriod, interchangeSequence, referenceMessageRecep))
            .collect(Collectors.toList());
    }

    private OutboundStateRepositoryExtensions.UpdateRecepParams prepareOutboundStateUpdateParams(
        String outbound_sender, String outbound_recipient, Instant dateTimePeriod, Long interchangeSequence, ReferenceMessageRecep referenceMessageRecep) {

        var messageSequence = referenceMessageRecep.getMessageSequenceNumber();
        var recepCode = referenceMessageRecep.getRecepCode();

        return new OutboundStateRepositoryExtensions.UpdateRecepParams(
            outbound_sender, outbound_recipient, interchangeSequence, messageSequence, recepCode, dateTimePeriod);
    }

    private List<Message> filterOutDuplicates(Interchange interchange) {
        return interchange.getMessages().stream()
            .filter(message -> {
                boolean hasBeenProcessed = hasAlreadyBeenProcessed(message);
                if (hasBeenProcessed) {
                    LOGGER.info("Skipping message {} as it has already been processed", message);
                }
                return !hasBeenProcessed;
            })
            .collect(Collectors.toList());
    }

    private boolean hasAlreadyBeenProcessed(Message message) {
        var interchangeHeader = message.getInterchange().getInterchangeHeader();
        var messageHeader = message.getMessageHeader();

        var inboundState = inboundStateRepository.findBy(
            WorkflowId.RECEP,
            interchangeHeader.getSender(),
            interchangeHeader.getRecipient(),
            interchangeHeader.getSequenceNumber(),
            messageHeader.getSequenceNumber(),
            null);

        return inboundState.isPresent();
    }
}
