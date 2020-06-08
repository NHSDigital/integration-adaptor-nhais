package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.FromFhirToEdifact;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.*;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirToEdifactService {

    private final FhirParser fhirParser;
    private final OutboundStateRepository outboundStateRepository;
    private final SequenceService sequenceService;
    private final TimestampService timestampService;

    public TranslatedInterchange convertToEdifact(Parameters parameters, ReferenceTransactionType.TransactionType transactionType) throws FhirValidationException, EdifactValidationException {
        TranslationItems translationItems = new TranslationItems();
        translationItems.patient = fhirParser.getPatientFromParams(parameters);
        translationItems.transactionType = transactionType;
        extractDetailsFromPatient(translationItems);
        generateTimestamp(translationItems);
        createSegments(parameters, translationItems);
        prevalidateSegments(translationItems);
        generateSequenceNumbers(translationItems);
        setOperationId(translationItems);
        recordOutboundState(translationItems);
        addSequenceNumbersToSegments(translationItems);
        return translateInterchange(translationItems);
    }

    private void setOperationId(TranslationItems translationItems) {
        translationItems.operationId = OperationId.buildOperationId(translationItems.recipient, translationItems.transactionNumber);
    }

    private void extractDetailsFromPatient(TranslationItems translationItems) throws FhirValidationException {
        // set sender and recipient
        translationItems.sender = getSender(translationItems.patient);
        translationItems.recipient = getRecipient(translationItems.patient);
    }

    private String getSender(Patient patient) {
        return getOrganizationIdentifier(patient.getGeneralPractitionerFirstRep());
    }

    private String getRecipient(Patient patient) {
        return getOrganizationIdentifier(patient.getManagingOrganization());
    }

    private String getOrganizationIdentifier(Reference gpReference) {
        return gpReference.getReference().split("/")[1];
    }

    private void createSegments(Parameters parameters, TranslationItems translationItems) {
        FromFhirToEdifact fromFhirToEdifact = new FromFhirToEdifact();

        translationItems.segments.add(new InterchangeHeader(translationItems.sender, translationItems.recipient, translationItems.translationTimestamp));
        translationItems.segments.add(new MessageHeader());
        translationItems.segments.add(new BeginningOfMessage());
        translationItems.segments.addAll(fromFhirToEdifact.map(parameters));
        translationItems.segments.add(new SegmentGroup(1));
        translationItems.segments.add(new ReferenceTransactionNumber());
        translationItems.segments.add(new MessageTrailer(8));
        translationItems.segments.add(new InterchangeTrailer(1));
    }

    private void prevalidateSegments(TranslationItems translationItems) throws EdifactValidationException {
        for (Segment segment : translationItems.segments) {
            segment.preValidate();
        }
    }

    private void generateSequenceNumbers(TranslationItems translationItems) {
        translationItems.sendInterchangeSequence =
                sequenceService.generateInterchangeId(translationItems.sender, translationItems.recipient);
        translationItems.sendMessageSequence =
                sequenceService.generateMessageId(translationItems.sender, translationItems.recipient);
        translationItems.transactionNumber =
                sequenceService.generateTransactionId();
    }

    private void generateTimestamp(TranslationItems translationItems) {
        translationItems.translationTimestamp = timestampService.getCurrentTimestamp();
    }

    private void recordOutboundState(TranslationItems translationItems) {
        var outboundState = new OutboundState()
                .setRecipient(translationItems.recipient)
                .setSender(translationItems.sender)

                .setSendInterchangeSequence(translationItems.sendInterchangeSequence)
                .setSendMessageSequence(translationItems.sendMessageSequence)
                .setTransactionId(translationItems.transactionNumber)

                .setTransactionType(translationItems.transactionType.getAbbreviation())
                .setTransactionTimestamp(Date.from(translationItems.translationTimestamp))
                .setOperationId(translationItems.operationId);
        outboundStateRepository.save(outboundState);
    }

    private void addSequenceNumbersToSegments(TranslationItems translationItems) {
        for (Segment segment : translationItems.segments) {
            if (segment instanceof InterchangeHeader) {
                InterchangeHeader interchangeHeader = (InterchangeHeader) segment;
                interchangeHeader.setSequenceNumber(translationItems.sendInterchangeSequence);
            } else if (segment instanceof InterchangeTrailer) {
                InterchangeTrailer interchangeTrailer = (InterchangeTrailer) segment;
                interchangeTrailer.setSequenceNumber(translationItems.sendInterchangeSequence);
            } else if (segment instanceof MessageHeader) {
                MessageHeader messageHeader = (MessageHeader) segment;
                messageHeader.setSequenceNumber(translationItems.sendMessageSequence);
            } else if (segment instanceof MessageTrailer) {
                MessageTrailer messageTrailer = (MessageTrailer) segment;
                messageTrailer.setSequenceNumber(translationItems.sendMessageSequence);
            } else if (segment instanceof ReferenceTransactionNumber) {
                ReferenceTransactionNumber referenceTransactionNumber = (ReferenceTransactionNumber) segment;
                referenceTransactionNumber.setTransactionNumber(translationItems.transactionNumber);
            }
        }
    }

    private TranslatedInterchange translateInterchange(TranslationItems translationItems) throws EdifactValidationException {
        List<String> segmentStrings = new ArrayList<>(translationItems.segments.size());
        for (Segment segment : translationItems.segments) {
            segmentStrings.add(segment.toEdifact());
        }
        String edifact = String.join("\n", segmentStrings);
        TranslatedInterchange interchange = new TranslatedInterchange();
        interchange.setEdifact(edifact);
        interchange.setInterchangeType(TranslatedInterchange.InterchangeType.REGISTRATION);
        interchange.setOperationId(translationItems.operationId);
        return interchange;
    }

    private static class TranslationItems {
        private Patient patient;
        private ReferenceTransactionType.TransactionType transactionType;
        private List<Segment> segments = new ArrayList<>();
        private String sender;
        private String recipient;
        private String operationId;
        private Long sendMessageSequence;
        private Long sendInterchangeSequence;
        private Long transactionNumber;
        private Instant translationTimestamp;
    }
}
