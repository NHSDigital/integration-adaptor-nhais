package uk.nhs.digital.nhsconnect.nhais.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateFactory;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepositoryExtensions;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

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
    private final InboundStateFactory inboundStateFactory;
    private final TimestampService timestampService;

    public void handleRecep(InboundMeshMessage meshMessage) {
        LOGGER.info("Processing inbound RECEP interchange");
        LOGGER.debug("Received RECEP message: {}", meshMessage);
        var recep = edifactParser.parse(meshMessage.getContent());
        LOGGER.debug("Parsed RECEP message into: {}", recep);

        var messagesToProcess = filterOutDuplicates(recep);

        var outboundStateUpdates = prepareOutboundStateUpdates(messagesToProcess);
        var inboundStateInserts = prepareInboundStateInserts(messagesToProcess);

        updateOutboundStateWithRecepDetails(outboundStateUpdates);
        insertInboundState(inboundStateInserts);
    }

    private void insertInboundState(List<InboundState> inboundStateInserts) {
        LOGGER.info("Inserting {} InboundState records for the RECEP interchange", inboundStateInserts.size());
        LOGGER.debug("Inserting InboundState records {}", inboundStateInserts);
        inboundStateInserts.forEach(inboundStateRepository::save);
    }

    private void updateOutboundStateWithRecepDetails(List<OutboundStateRepositoryExtensions.UpdateRecepParams> updateRecepParams) {
        LOGGER.info("Updating {} OutboundState records with RECEP details", updateRecepParams.size());
        updateRecepParams.forEach(updateRecepParam -> {
            outboundStateRepository.updateRecepDetails(updateRecepParam).ifPresentOrElse(
                outboundState -> LOGGER.debug("Updated outbound state RECEP details using {}", updateRecepParam),
                () -> LOGGER.warn("No outbound state document was updated with RECEP details using {}", updateRecepParam)
            );
        });
    }

    private List<InboundState> prepareInboundStateInserts(List<Message> recepMessages) {
        return recepMessages.stream()
            .map(inboundStateFactory::fromRecep)
            .collect(Collectors.toList());
    }

    private List<OutboundStateRepositoryExtensions.UpdateRecepParams> prepareOutboundStateUpdates(List<Message> messagesToProcess) {
        return messagesToProcess.stream()
            .map(this::prepareOutboundStateUpdateParams)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<OutboundStateRepositoryExtensions.UpdateRecepParams> prepareOutboundStateUpdateParams(Message message) {
        return message.getReferenceMessageReceps().stream()
            .map(referenceMessageRecep -> prepareOutboundStateUpdateParams(message, referenceMessageRecep))
            .collect(Collectors.toList());
    }

    private OutboundStateRepositoryExtensions.UpdateRecepParams prepareOutboundStateUpdateParams(Message message, ReferenceMessageRecep referenceMessageRecep) {
        //sender is swapped with recipient as communication is done the opposite way
        var outboundSender = message.getInterchange().getInterchangeHeader().getRecipient();
        var outboundRecipient = message.getInterchange().getInterchangeHeader().getSender();

        // sequence number of the interchange in which the RECEP was transmitted
        var recepInterchangeSequence = message.getInterchange().getInterchangeHeader().getSequenceNumber();
        var recepTranslationTimestamp = message.getInterchange().getInterchangeHeader().getTranslationTime();
        var referencedInterchangeSequence = message.getReferenceInterchangeRecep().getInterchangeSequenceNumber();
        var referencedMessageSequence = referenceMessageRecep.getMessageSequenceNumber();
        var recepDocument = new OutboundState.Recep()
            .setCode(referenceMessageRecep.getRecepCode())
            .setTranslationTimestamp(recepTranslationTimestamp)
            .setInterchangeSequence(recepInterchangeSequence)
            .setProcessedTimestamp(timestampService.getCurrentTimestamp());
        return new OutboundStateRepositoryExtensions.UpdateRecepParams(
            outboundSender, outboundRecipient, referencedInterchangeSequence, referencedMessageSequence, recepDocument);
    }

    private List<Message> filterOutDuplicates(Interchange interchange) {
        return interchange.getMessages().stream()
            .filter(message -> {
                boolean hasBeenProcessed = hasAlreadyBeenProcessed(message);
                if (hasBeenProcessed) {
                    LOGGER.warn("Skipping message {} as it has already been processed", message);
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
