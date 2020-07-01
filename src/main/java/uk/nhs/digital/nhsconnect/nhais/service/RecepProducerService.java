package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DateTimePeriod;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepBeginningOfMessage;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepMessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RecepNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceInterchangeRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecepProducerService {

    private final SequenceService sequenceService;
    private final TimestampService timestampService;

    public String produceRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        var segments = mapEdifactToRecep(receivedInterchangeFromHa);
        return toEdifact(segments);
    }

    public String toEdifact(List<Segment> segments) {
        return segments.stream()
            .map(Segment::toEdifact)
            .collect(Collectors.joining("\n"));
    }

    private List<Segment> mapEdifactToRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        List<Segment> recepMessageSegments = new ArrayList<>();

        var recepInterchangeHeader = mapToRecipInterchangeHeader(receivedInterchangeFromHa);
        var recepMessageHeader = mapToRecipMessageHeader(receivedInterchangeFromHa);

        var referenceMessageRecepSegments = receivedInterchangeFromHa.getMessages().stream()
            .map(this::mapToReferenceMessageRecep)
            .collect(Collectors.toList());

        recepMessageSegments.add(recepInterchangeHeader);
        recepMessageSegments.add(recepMessageHeader);
        var recepBeginningOfMessage = mapToRecepBeginningOfMessage(receivedInterchangeFromHa);
        recepMessageSegments.add(recepBeginningOfMessage);
        recepMessageSegments.add(mapToRecepNameAndAddress(receivedInterchangeFromHa));
        var recepTranslationDateTime = mapToRecepTranslationDateTime(receivedInterchangeFromHa);
        recepMessageSegments.add(recepTranslationDateTime);
        recepMessageSegments.addAll(referenceMessageRecepSegments);
        recepMessageSegments.add(mapToReferenceInterchangeRecep(receivedInterchangeFromHa));

        var recepMessageTrailer = mapToRecepMessageTrailer();
        var recepInterchangeTrailer = mapToRecepInterchangeTrailer(receivedInterchangeFromHa);

        recepMessageSegments.add(recepMessageTrailer);
        recepMessageSegments.add(recepInterchangeTrailer);

        recepMessageSegments.forEach(Segment::preValidate);

        var recepInterchangeSequence = sequenceService.generateInterchangeId(
            recepInterchangeHeader.getSender(),
            recepInterchangeHeader.getRecipient());
        var recepMessageSequence = sequenceService.generateMessageId(
            recepInterchangeHeader.getSender(),
            recepInterchangeHeader.getRecipient());

        recepInterchangeHeader.setSequenceNumber(recepInterchangeSequence);
        recepInterchangeTrailer
            .setSequenceNumber(recepInterchangeSequence)
            .setNumberOfMessages(1); // we always build recep with single message inside

        recepMessageHeader.setSequenceNumber(recepMessageSequence);
        recepMessageTrailer
            .setSequenceNumber(recepMessageSequence)
            .setNumberOfSegments(recepMessageSegments.size() - 2); // excluding UNB and UNZ

        var currentTimestamp = timestampService.getCurrentTimestamp();
        recepBeginningOfMessage.setTimestamp(currentTimestamp);
        recepTranslationDateTime.setTimestamp(currentTimestamp);

        recepMessageSegments.forEach(Segment::validate);

        return recepMessageSegments;
    }

    private InterchangeTrailer mapToRecepInterchangeTrailer(Interchange receivedInterchangeFromHa) {
        return new InterchangeTrailer(receivedInterchangeFromHa.getInterchangeTrailer().getNumberOfMessages());
    }

    private MessageTrailer mapToRecepMessageTrailer() {
        return new MessageTrailer(0);
    }

    private DateTimePeriod mapToRecepTranslationDateTime(Interchange receivedInterchangeFromHa) {
        return new DateTimePeriod(null, DateTimePeriod.TypeAndFormat.TRANSLATION_TIMESTAMP);
    }

    private RecepHeader mapToRecipInterchangeHeader(Interchange interchange) {
        var recepSender = interchange.getInterchangeHeader().getRecipient();
        var recepRecipient = interchange.getInterchangeHeader().getSender();

        return new RecepHeader(recepSender, recepRecipient, interchange.getInterchangeHeader().getTranslationTime());
    }

    private RecepMessageHeader mapToRecipMessageHeader(Interchange interchange) {
        return new RecepMessageHeader();
    }

    private RecepBeginningOfMessage mapToRecepBeginningOfMessage(Interchange interchange) {
        return new RecepBeginningOfMessage();
    }

    private RecepNameAndAddress mapToRecepNameAndAddress(Interchange interchange) {
        return new RecepNameAndAddress(interchange.getInterchangeHeader().getSender());
    }

    private ReferenceMessageRecep mapToReferenceMessageRecep(Message message) {
        return new ReferenceMessageRecep(
            message.getMessageHeader().getSequenceNumber(), ReferenceMessageRecep.RecepCode.SUCCESS);
    }

    private ReferenceInterchangeRecep mapToReferenceInterchangeRecep(Interchange interchange) {
        return new ReferenceInterchangeRecep(
            interchange.getInterchangeHeader().getSequenceNumber(),
            ReferenceInterchangeRecep.RecepCode.RECEIVED,
            interchange.getInterchangeTrailer().getNumberOfMessages());
    }

}