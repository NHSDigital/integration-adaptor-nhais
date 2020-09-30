package uk.nhs.digital.nhsconnect.nhais.outbound;

import lombok.AllArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshCypherDecoder;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.RegistrationMessageDateTime;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.sequence.SequenceService;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public abstract class AbstractToEdifactService<T extends CommonTranslationItems> {

    protected final SequenceService sequenceService;
    protected final TimestampService timestampService;
    protected final OutboundStateRepository outboundStateRepository;
    protected final MeshCypherDecoder meshCypherDecoder;

    protected abstract List<Segment> createMessageSegments(T translationItems);

    protected OutboundMeshMessage convert(T translationItems) {
        Objects.requireNonNull(translationItems.getSender(), "Sender can't be null");
        Objects.requireNonNull(translationItems.getRecipient(), "Recipient can't be null");
        Objects.requireNonNull(translationItems.getTransactionType(), "Transaction type can't be null");

        validateRecipient(translationItems);
        generateTimestamp(translationItems);
        createSegments(translationItems);
        prevalidateSegments(translationItems);
        generateSequenceNumbers(translationItems);
        setOperationId(translationItems);
        recordOutboundState(translationItems);
        addSequenceNumbersToSegments(translationItems);
        return translateInterchange(translationItems);
    }

    protected void validateRecipient(T translationItems) {
        meshCypherDecoder.validateRecipient(translationItems.getRecipient());
    }

    protected void generateTimestamp(T translationItems) {
        translationItems.setTranslationTimestamp(timestampService.getCurrentTimestamp());
    }

    protected void createSegments(T translationItems) throws FhirValidationException {
        var segments = new ArrayList<Segment>();
        segments.add(new InterchangeHeader(translationItems.getSender(), translationItems.getRecipient(), translationItems.getTranslationTimestamp()));
        segments.add(new MessageHeader());
        List<Segment> messageSegments = createMessageSegments(translationItems);
        segments.addAll(messageSegments);
        // numberOfSegments must include the header and trailer thus numberOfSegments = size() + 2
        segments.add(new MessageTrailer(messageSegments.size() + 2));
        // outbound interchanges always contain a single message thus numberOfMessages = 1
        segments.add(new InterchangeTrailer(1));
        translationItems.setSegments(segments);
    }

    protected void prevalidateSegments(T translationItems) throws EdifactValidationException {
        for (Segment segment : translationItems.getSegments()) {
            segment.preValidate();
        }
    }

    protected void generateSequenceNumbers(T translationItems) {
        translationItems.setSendInterchangeSequence(
            sequenceService.generateInterchangeSequence(translationItems.getSender(), translationItems.getRecipient()));
        translationItems.setSendMessageSequence(
            sequenceService.generateMessageSequence(translationItems.getSender(), translationItems.getRecipient()));
        translationItems.setTransactionNumber(
            sequenceService.generateTransactionNumber(translationItems.getSender()));
    }

    protected void setOperationId(T translationItems) {
        translationItems.setOperationId(OperationId.buildOperationId(translationItems.getSender(), translationItems.getTransactionNumber()));
    }

    protected void recordOutboundState(T translationItems) {
        var outboundState = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setRecipient(translationItems.getRecipient())
            .setSender(translationItems.getSender())

            .setInterchangeSequence(translationItems.getSendInterchangeSequence())
            .setMessageSequence(translationItems.getSendMessageSequence())
            .setTransactionNumber(translationItems.getTransactionNumber())

                .setTransactionType(translationItems.getTransactionType())
                .setTranslationTimestamp(translationItems.getTranslationTimestamp())
                .setOperationId(translationItems.getOperationId());
        outboundStateRepository.save(outboundState);
    }

    protected void addSequenceNumbersToSegments(T translationItems) {
        for (Segment segment : translationItems.getSegments()) {
            if (segment instanceof InterchangeHeader) {
                InterchangeHeader interchangeHeader = (InterchangeHeader) segment;
                interchangeHeader.setSequenceNumber(translationItems.getSendInterchangeSequence());
            } else if (segment instanceof InterchangeTrailer) {
                InterchangeTrailer interchangeTrailer = (InterchangeTrailer) segment;
                interchangeTrailer.setSequenceNumber(translationItems.getSendInterchangeSequence());
            } else if (segment instanceof MessageHeader) {
                MessageHeader messageHeader = (MessageHeader) segment;
                messageHeader.setSequenceNumber(translationItems.getSendMessageSequence());
            } else if (segment instanceof MessageTrailer) {
                MessageTrailer messageTrailer = (MessageTrailer) segment;
                messageTrailer.setSequenceNumber(translationItems.getSendMessageSequence());
            } else if (segment instanceof ReferenceTransactionNumber) {
                ReferenceTransactionNumber referenceTransactionNumber = (ReferenceTransactionNumber) segment;
                referenceTransactionNumber.setTransactionNumber(translationItems.getTransactionNumber());
            } else if (segment instanceof RegistrationMessageDateTime) {
                RegistrationMessageDateTime registrationMessageDateTime = (RegistrationMessageDateTime) segment;
                registrationMessageDateTime.setTimestamp(translationItems.getTranslationTimestamp());
            }
        }
    }

    protected OutboundMeshMessage translateInterchange(T translationItems) throws EdifactValidationException {
        List<String> segmentStrings = new ArrayList<>(translationItems.getSegments().size());
        for (Segment segment : translationItems.getSegments()) {
            segmentStrings.add(segment.toEdifact());
        }
        String edifact = String.join("\n", segmentStrings);
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setContent(edifact);
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setOperationId(translationItems.getOperationId());
        meshMessage.setHaTradingPartnerCode(translationItems.getRecipient());
        return meshMessage;
    }

    protected String getRecipientTradingPartnerCode(String recipient) throws FhirValidationException {
        if (recipient.length() == 2) {
            return recipient + "01";
        } else {
            return recipient + "1";
        }
    }
}
