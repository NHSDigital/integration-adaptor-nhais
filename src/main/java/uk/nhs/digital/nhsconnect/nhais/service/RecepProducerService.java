package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecepProducerService {

    private final SequenceService sequenceService;

    public List<Segment> produceRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        //TODO: NIAD-390
//        var segments = mapEdifactToRecep(receivedInterchangeFromHa);
//
//        var edifact = toEdifact(segments);
//        var recepMessage = new RecepMessage(edifact);
//        return new Recep(recepMessage);
        throw new NotImplementedException();
    }

    public String toEdifact(List<Segment> segments) {
        return segments.stream()
            .map(Segment::toEdifact)
            .collect(Collectors.joining("\n"));
    }

//    private List<Segment> mapEdifactToRecep(InterchangeV2 receivedInterchangeFromHa) throws EdifactValidationException {
//        List<Segment> recepMessageSegments = new ArrayList<>();
//
//        var recepInterchangeHeader = mapToRecipInterchangeHeader(receivedInterchangeFromHa);
//        var recepMessageHeader = mapToRecipMessageHeader(receivedInterchangeFromHa);
//        var recepMessageTrailer = mapToRecepMessageTrailer(2);//TODO: change when multiple messages handling is implemented
//        var recepInterchangeTrailer = mapToRecepInterchangeTrailer(receivedInterchangeFromHa);
//
//        recepMessageSegments.add(recepInterchangeHeader);
//        recepMessageSegments.add(recepMessageHeader);
//        recepMessageSegments.add(mapToRecepBeginningOfMessage(receivedInterchangeFromHa));
//        recepMessageSegments.add(mapToRecepNameAndAddress(receivedInterchangeFromHa));
//        recepMessageSegments.add(mapToRecepTranslationDateTime(receivedInterchangeFromHa));
//        recepMessageSegments.add(mapToReferenceMessageRecep(receivedInterchangeFromHa));
//        recepMessageSegments.add(mapToReferenceInterchangeRecep(receivedInterchangeFromHa));
//        recepMessageSegments.add(recepMessageTrailer);
//        recepMessageSegments.add(recepInterchangeTrailer);
//
//        recepMessageSegments.forEach(Segment::preValidate);
//
//        var recepInterchangeSequence = sequenceService.generateInterchangeId(
//            recepInterchangeHeader.getSender(),
//            recepInterchangeHeader.getRecipient());
//        var recepMessageSequence = sequenceService.generateMessageId(
//            recepInterchangeHeader.getSender(),
//            recepInterchangeHeader.getRecipient());
//
//        recepInterchangeHeader.setSequenceNumber(recepInterchangeSequence);
//        recepInterchangeTrailer
//            .setSequenceNumber(recepInterchangeSequence)
//            .setNumberOfMessages(1); //TODO: change when multiple messages handling is implemented
//
//        recepMessageHeader.setSequenceNumber(recepMessageSequence);
//        recepMessageTrailer
//            .setSequenceNumber(recepMessageSequence)
//            .setNumberOfSegments(7); //TODO: change when multiple messages handling is implemented. How many segments are there in message in total, including header and trailer
//
//        recepMessageSegments.forEach(Segment::validate);
//
//        return recepMessageSegments;
//    }
//
//    private InterchangeTrailer mapToRecepInterchangeTrailer(Interchange receivedInterchangeFromHa) {
//        return new InterchangeTrailer(receivedInterchangeFromHa.getInterchangeTrailer().getNumberOfMessages());
//    }
//
//    private MessageTrailer mapToRecepMessageTrailer(int refSegmentCount) {
//        if (refSegmentCount < 2) {
//            throw new IllegalStateException("There should be at least 2 ref segments. 1..1 ref interchange and 1..N ref message");
//        }
//        // there are 5 const segments and variable count of ref segments
//        return new MessageTrailer(5 + refSegmentCount);
//    }
//
//    private DateTimePeriod mapToRecepTranslationDateTime(Interchange receivedInterchangeFromHa) {
//        return new DateTimePeriod(
//            receivedInterchangeFromHa.getTranslationDateTime().getTimestamp(),
//            receivedInterchangeFromHa.getTranslationDateTime().getTypeAndFormat());
//    }
//
//    private RecepHeader mapToRecipInterchangeHeader(Interchange interchange) {
//        var recepSender = interchange.getInterchangeHeader().getRecipient();
//        var recepRecipient = interchange.getInterchangeHeader().getSender();
//
//        return new RecepHeader(recepSender, recepRecipient, interchange.getInterchangeHeader().getTranslationTime());
//    }
//
//    private RecepMessageHeader mapToRecipMessageHeader(Interchange interchange) {
//        return new RecepMessageHeader()
//            .setSequenceNumber(interchange.getMessageHeader().getSequenceNumber());
//    }
//
//    private RecepBeginningOfMessage mapToRecepBeginningOfMessage(Interchange interchange) {
//        return new RecepBeginningOfMessage(interchange.getTranslationDateTime().getTimestamp());
//    }
//
//    private RecepNameAndAddress mapToRecepNameAndAddress(Interchange interchange) {
//        return new RecepNameAndAddress(interchange.getInterchangeHeader().getSender());
//    }
//
//    private ReferenceMessageRecep mapToReferenceMessageRecep(Interchange interchange) {
//        return new ReferenceMessageRecep(
//            interchange.getMessageHeader().getSequenceNumber(), ReferenceMessageRecep.RecepCode.SUCCESS);
//    }
//
//    private ReferenceInterchangeRecep mapToReferenceInterchangeRecep(Interchange interchange) {
//        return new ReferenceInterchangeRecep(
//            interchange.getInterchangeHeader().getSequenceNumber(),
//            ReferenceInterchangeRecep.RecepCode.RECEIVED,
//            interchange.getInterchangeTrailer().getNumberOfMessages());
//    }

}