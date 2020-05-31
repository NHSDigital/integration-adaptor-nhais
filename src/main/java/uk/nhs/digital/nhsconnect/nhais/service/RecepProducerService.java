package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.*;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepositoryExtensions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class RecepProducerService {
    private static final String UNB_TRANSFER_SUFFIX = "+RECEP+++EDIFACT TRANSFER";
    private final OutboundStateRepository outboundStateRepository;

    public Interchange produceRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        Interchange recepInterchange = new Interchange(mapEdifactToRecep(receivedInterchangeFromHa));
        recordOutboundState(recepInterchange);

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
        recepMessageSegments.add(receivedInterchangeFromHa.getMessageTrailer());
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

    private InterchangeHeader mapToRecipInterchangeHeader(Interchange interchange) {
        InterchangeHeader interchangeHeader = interchange.getInterchangeHeader();
        interchangeHeader.setTransferString(UNB_TRANSFER_SUFFIX);

        return interchangeHeader;
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
        // Please check InterchangeTrailer.java comment
        // forEach might be needed here:
        return List.of(ReferenceMessageRecep.builder()
                .messageSequenceNumber(interchange.getMessageHeader().getSequenceNumber())
                .recepCode(ReferenceMessageRecep.RecepCode.CP)
                .build());
    }

    private ReferenceInterchangeRecep mapToReferenceInterchangeRecep(Interchange interchange) {
        return new ReferenceInterchangeRecep(
                interchange.getInterchangeHeader().getSequenceNumber(),
                interchange.getInterchangeTrailer().getNumberOfMessages());
    }

}