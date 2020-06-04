package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Recep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepBeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepMessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class RecepProducerService {

    public Recep produceRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        var segments = mapEdifactToRecep(receivedInterchangeFromHa);
        var edifact = toEdifact(segments);
        var recepMessage = new RecepMessage(edifact);
        return new Recep(recepMessage);
    }

    private String toEdifact(List<Segment> segments) {
        return segments.stream()
            .map(Segment::toEdifact)
            .collect(Collectors.joining("\n"));
    }

    private List<Segment> mapEdifactToRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        List<Segment> recepMessageSegments = new ArrayList<>();
        recepMessageSegments.add(mapToRecipInterchangeHeader(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToRecipMessageHeader(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToRecepBeginningOfMessage(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToRecepNameAndAdress(receivedInterchangeFromHa));
        recepMessageSegments.add(receivedInterchangeFromHa.getTranslationDateTime());
        recepMessageSegments.addAll(mapToReferenceMessageRecep(receivedInterchangeFromHa));
        recepMessageSegments.add(mapToReferenceInterchangeRecep(receivedInterchangeFromHa));
        recepMessageSegments.add(receivedInterchangeFromHa.getMessageTrailer().get(0));
        recepMessageSegments.add(receivedInterchangeFromHa.getInterchangeTrailer());

        return recepMessageSegments;
    }

    private RecepHeader mapToRecipInterchangeHeader(Interchange interchange) {
        return new RecepHeader(
                interchange.getInterchangeHeader().getSender(),
                interchange.getInterchangeHeader().getRecipient(),
                interchange.getInterchangeHeader().getTranslationTime(),
                interchange.getInterchangeHeader().getSequenceNumber()); // this should be our new sequence from db
    }

    private RecepMessageHeader mapToRecipMessageHeader(Interchange interchange) {
        RecepMessageHeader recepMessageHeader = new RecepMessageHeader();
        recepMessageHeader.setSequenceNumber(interchange.getMessageHeader().getSequenceNumber());

        return recepMessageHeader;
    }

    private RecepBeginningOfMessage mapToRecepBeginningOfMessage(Interchange interchange) {
        return new RecepBeginningOfMessage(interchange.getTranslationDateTime().getTimestamp());
    }

    private RecepNameAndAddress mapToRecepNameAndAdress(Interchange interchange) {
        return new RecepNameAndAddress(interchange.getInterchangeHeader().getSender());
    }

    private List<ReferenceMessageRecep> mapToReferenceMessageRecep(Interchange interchange) {
        return interchange.getMessageTrailer().stream()
                .map(messageTrailer -> new ReferenceMessageRecep(
                    messageTrailer.getSequenceNumber(), ReferenceMessageRecep.RecepCode.SUCCESS))
                .collect(Collectors.toList());
    }

    private ReferenceInterchangeRecep mapToReferenceInterchangeRecep(Interchange interchange) {
        return new ReferenceInterchangeRecep(
            interchange.getInterchangeHeader().getSequenceNumber(),
            ReferenceInterchangeRecep.RecepCode.RECEIVED,
            interchange.getInterchangeTrailer().getNumberOfMessages());
    }

}