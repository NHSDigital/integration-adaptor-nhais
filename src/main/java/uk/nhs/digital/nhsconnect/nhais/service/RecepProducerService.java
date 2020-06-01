package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepBeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepMessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class RecepProducerService {
    private final OutboundStateRepository outboundStateRepository;

    public Interchange produceRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        Interchange recepInterchange = new Interchange(mapEdifactToRecep(receivedInterchangeFromHa));
        recordOutboundState(receivedInterchangeFromHa);

        return recepInterchange;
    }

    private List<Segment> mapEdifactToRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        List<Segment> recepMessageSegments = new ArrayList<>();
        recepMessageSegments.add(mapToRecipInterchangeHeader(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToRecipMessageHeader(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToRecepBeginningOfMessage(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToRecepNameAndAdress(receivedInterchangeFromHa));
        recepMessageSegments.add(receivedInterchangeFromHa.getDateTimePeriod());
        recepMessageSegments.addAll(mapToReferenceMessageRecep(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToReferenceInterchangeRecep(receivedInterchangeFromHa));
        recepMessageSegments.add(receivedInterchangeFromHa.getMessageTrailer().get(0));
        recepMessageSegments.add(receivedInterchangeFromHa.getInterchangeTrailer());

        return recepMessageSegments;
    }

    private void recordOutboundState(Interchange interchange) {
        String sender = interchange.getInterchangeHeader().getSender();
        String recipient = interchange.getInterchangeHeader().getRecipient();
        Instant dateTimePeriod = interchange.getDateTimePeriod().getTimestamp();
        long interchangeSequence = interchange.getInterchangeHeader().getSequenceNumber();

        for (ReferenceMessageRecep referenceMessageRecep : interchange.getReferenceMessageReceps()) {
            long messageSequence = referenceMessageRecep.getMessageSequenceNumber();
            ReferenceMessageRecep.RecepCode recepCode = referenceMessageRecep.getRecepCode();

            var queryParams = new OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams(
                    sender, recipient, interchangeSequence, messageSequence);
            var recepDetails = new OutboundStateRepositoryExtensions.UpdateRecepDetails(
                    recepCode, dateTimePeriod);

            var updatedOutboundState = outboundStateRepository.updateRecepDetails(queryParams, recepDetails);
            LOGGER.debug("Updated outbound state {} using query {} and recep details {}", updatedOutboundState, queryParams, recepDetails);
        }
    }

    private RecepHeader mapToRecipInterchangeHeader(Interchange interchange) {
        return new RecepHeader(
                interchange.getInterchangeHeader().getSender(),
                interchange.getInterchangeHeader().getRecipient(),
                interchange.getInterchangeHeader().getTranslationTime(),
                interchange.getInterchangeHeader().getSequenceNumber());
    }

    private RecepMessageHeader mapToRecipMessageHeader(Interchange interchange) {
        RecepMessageHeader recepMessageHeader = new RecepMessageHeader();
        recepMessageHeader.setSequenceNumber(interchange.getMessageHeader().getSequenceNumber());

        return recepMessageHeader;
    }

    private RecepBeginningOfMessage mapToRecepBeginningOfMessage(Interchange interchange) {
        return new RecepBeginningOfMessage(interchange.getDateTimePeriod().getTimestamp());
    }

    private RecepNameAndAddress mapToRecepNameAndAdress(Interchange interchange) {
        return new RecepNameAndAddress(interchange.getInterchangeHeader().getSender());
    }

    private List<ReferenceMessageRecep> mapToReferenceMessageRecep(Interchange interchange) {
        return interchange.getMessageTrailer().stream()
                .map(messageTrailer -> ReferenceMessageRecep.builder()
                        .messageSequenceNumber(messageTrailer.getSequenceNumber())
                        .recepCode(ReferenceMessageRecep.RecepCode.CP)
                        .build())
                .collect(Collectors.toList());
    }

    private ReferenceInterchangeRecep mapToReferenceInterchangeRecep(Interchange interchange) {
        return new ReferenceInterchangeRecep(
                interchange.getInterchangeHeader().getSequenceNumber(),
                interchange.getInterchangeTrailer().getNumberOfMessages());
    }

}